package com.talha11bu.silkroad.services;

import com.talha11bu.silkroad.model.*;
import com.talha11bu.silkroad.repo.*;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Core service orchestrating all session lifecycle operations.
 *
 * <p>Manages creation, joining, rejoining, and termination of ephemeral
 * file-sharing sessions. Coordinates between the database repositories,
 * JWT authentication, R2 cloud storage, and WebSocket notifications.</p>
 *
 * <p>Also runs a scheduled cleanup job to purge expired and abandoned sessions.</p>
 *
 * @see com.talha11bu.silkroad.services.R2Service
 * @see com.talha11bu.silkroad.services.NotiffService
 * @see com.talha11bu.silkroad.services.JwtTokenService
 */
@Service
public class SessionService {

    @Autowired
    private SessionRepo sessionRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private FilesRepo filesRepo;
    @Autowired
    private SessionIdGenerator idGenerator;
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private NotiffService notiffService;
    @Autowired
    private R2Service r2Service;

    /**
     * Initializes a new secure file-sharing session.
     * Generates a unique session ID, sets the expiration based on the requested
     * duration,
     * securely hashes the password, and creates the host's authentication JWT
     * token.
     *
     * @param request Contains the desired username, password, and session duration.
     * @return CreateResponse containing the JWT token, session details, and success
     *         status.
     */
    @Transactional
    public CreateResponse createSession(CreateRequest request) {
        try {
            String newSessionId = idGenerator.generatedId();
            var expiration = Instant.now().plus(request.duration());

            Session newSession = new Session(newSessionId, request.password(), expiration);
            Session savedSession = sessionRepo.save(newSession);

            String token = registerUser(savedSession, request.username());

            return new CreateResponse(true, token, savedSession, request.duration());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new CreateResponse(false, null, null, null);
        }
    }

    /**
     * Attempts to join an active session.
     * Validates the session existence, password match, and ensures the session
     * hasn't expired.
     * Upon success, generates a JWT for the joining user and broadcasts a
     * 'USER_JOINED' notification via WebSocket.
     *
     * @param request Contains the session ID to join, the password, and the
     *                username.
     * @return JoinResponse containing the JWT token, session state, and remaining
     *         time.
     */
    @Transactional
    public JoinResponse joinSession(JoinRequest request) {
        try {
            Session session = sessionRepo.findById(request.sessionId()).orElseThrow();

            if (!request.password().equals(session.getPassword()) || session.isExpired()) {
                return new JoinResponse(false, null, null, null);
            }

            Duration timeLeft = Duration.between(Instant.now(), session.getExpiresAt());
            if (timeLeft.isNegative()) {
                return new JoinResponse(false, null, null, null);
            }

            String token = registerUser(session, request.username());

            SessionNotiff joinNotification = new SessionNotiff(SessionNotiff.NotifyType.USER_JOINED,
                    session.getSessionId(), request.username());

            notiffService.notifySessionMembers(session.getSessionId(), joinNotification);

            return new JoinResponse(true, token, session, timeLeft);

        } catch (Exception e) {
            return new JoinResponse(false, null, null, null);
        }
    }

    /**
     * Generates a JWT token for a user, persists the user entity, and assigns
     * them to the session's WebSocket topic.
     *
     * @param session  the session the user is being registered into.
     * @param username the user's display name.
     * @return the signed JWT token for the user.
     */
    private String registerUser(Session session, String username) {
        String token = jwtTokenService.generateToken(session.getSessionId(), username);
        Users user = new Users(username, token, "/topic/session/" + session.getSessionId(), session);
        userRepo.save(user);
        return token;
    }

    /**
     * Allows a previously authenticated user to rejoin a session using their existing JWT.
     * Useful for reconnecting after a page refresh or connection drop.
     *
     * @param token the user's existing JWT token.
     * @return JoinResponse with the current session state and remaining time.
     */
    public JoinResponse rejoinSession(String token) {
        Claims claims = jwtTokenService.validateAndParseToken(token);
        String sessionId = claims.get("sid", String.class);

        try {
            Session session = sessionRepo.findById(sessionId).orElseThrow();
            Duration timeLeft = Duration.between(Instant.now(), session.getExpiresAt());

            return new JoinResponse(true, token, session, timeLeft);
        } catch (Exception e) {
            return new JoinResponse(false, null, null, null);
        }
    }

    /**
     * Retrieves a pre-signed URL from the R2Service to allow direct client-side
     * uploads.
     * Validates the user's JWT to ensure they belong to the targeted session.
     *
     * @param sessionId   The ID of the session.
     * @param fileName    The original name of the file to be uploaded.
     * @param contentType The MIME type of the file.
     * @param token       The JWT token of the requesting user.
     * @return A map containing the upload URL and the R2 file key.
     */
    public java.util.Map<String, String> getPreSignedUploadUrl(String sessionId, String fileName, String contentType,
            String token) {
        Claims claims = jwtTokenService.validateAndParseToken(token);
        String tokenSessionId = claims.get("sid", String.class);

        if (tokenSessionId == null || !tokenSessionId.equals(sessionId)) {
            throw new SecurityException("You do not have access to this session.");
        }

        sessionRepo.findById(sessionId).orElseThrow(() -> new NoSuchElementException("Session not found."));

        return r2Service.generatePreSignedUploadUrl(sessionId, fileName, contentType);
    }

    /**
     * Confirms that a file has been successfully uploaded to R2 directly by the
     * client.
     * Validates the JWT, registers the file entity in the database, and broadcasts
     * a 'FILE_UPLOADED' WebSocket notification.
     *
     * @param sessionId The active session ID.
     * @param fileName  The original filename shown to users.
     * @param fileKey   The internal R2 storage key.
     * @param fileSize  The size of the file in bytes.
     * @param token     The JWT token confirming user authorization.
     * @return UploadResponse indicating success.
     */
    @Transactional
    public UploadResponse confirmFileUpload(String sessionId, String fileName, String fileKey, long fileSize,
            String token) {
        Claims claims = jwtTokenService.validateAndParseToken(token);
        String tokenSessionId = claims.get("sid", String.class);

        if (tokenSessionId == null || !tokenSessionId.equals(sessionId)) {
            throw new SecurityException("You do not have access to this session.");
        }

        Session session = sessionRepo.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found."));

        Files newFileEntity = new Files(fileName, fileKey, session);
        filesRepo.save(newFileEntity);

        SessionNotiff fileNotification = new SessionNotiff(SessionNotiff.NotifyType.FILE_UPLOADED, sessionId, fileName);
        notiffService.notifySessionMembers(sessionId, fileNotification);

        return new UploadResponse(fileName, "", fileSize);
    }

    /**
     * Downloads a single file from R2 after validating the session password.
     *
     * @param sessionId the ID of the session.
     * @param password  the session password for authorization.
     * @param filename  the original filename to download.
     * @return the file as a Spring {@link Resource}.
     * @throws NoSuchElementException if the file is not found.
     * @throws SecurityException      if the password is wrong or the session has expired.
     */
    public Resource downloadFile(String sessionId, String password, String filename) {
        Files file = filesRepo.findByFileNameAndSessionSessionId(filename, sessionId).orElseThrow(
                () -> new NoSuchElementException("File " + filename + " not found in session " + sessionId));

        Session session = file.getSession();
        if (!session.getPassword().equals(password))
            throw new SecurityException("Invalid Password");

        if (session.isExpired())
            throw new SecurityException("Cannot download File Session Expired");

        try {
            return r2Service.downloadFile(file.getR2Key());
        } catch (Exception e) {
            throw new RuntimeException("Something went Bad");
        }
    }

    /**
     * Generates a time-limited pre-signed download URL for a specific file.
     * Validates the user's JWT to ensure they belong to the targeted session.
     *
     * @param sessionId the ID of the session.
     * @param fileName  the original filename to generate a URL for.
     * @param token     the JWT token of the requesting user.
     * @return the pre-signed download URL, or {@code null} if the file is not found.
     */
    public String getPreSignedUrlForFile(String sessionId, String fileName, String token) {
        // 1. Verify the user is authenticated for this specific session
        Claims claims = jwtTokenService.validateAndParseToken(token);
        String tokenSessionId = claims.get("sid", String.class);

        if (tokenSessionId == null || !tokenSessionId.equals(sessionId)) {
            throw new SecurityException("You do not have access to this session's files.");
        }
        try {
            Files file = filesRepo.findByFileNameAndSessionSessionId(fileName, sessionId).orElseThrow();

            return r2Service.generatePreSignedDownloadUrl(file.getR2Key(), file.getFileName());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Downloads all files in a session as a single ZIP archive.
     * Validates the session password and expiration before streaming.
     *
     * @param sessionId the ID of the session.
     * @param password  the session password for authorization.
     * @return a {@link Resource} containing the streamed ZIP archive.
     * @throws NoSuchElementException if the session is not found or has no files.
     * @throws SecurityException      if the password is wrong or the session has expired.
     */
    public Resource downloadAllFilesAsZip(String sessionId, String password) {
        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found"));

        if (!session.getPassword().equals(password))
            throw new SecurityException("Invalid Password");

        if (session.isExpired())
            throw new SecurityException("Cannot download Files Session Expired");

        List<String> r2Keys = session.getFiles().stream().map(Files::getR2Key).toList();

        if (r2Keys.isEmpty()) {
            throw new NoSuchElementException("No files available to download");
        }
        try {
            return r2Service.donwloadFilesAsZip(r2Keys);
        } catch (IOException e) {
            throw new RuntimeException("Error creating ZIP File from R2", e);
        }
    }

    /**
     * Terminates a session by deleting all associated R2 files and removing the session
     * from the database. Broadcasts a closure notification to all connected clients.
     *
     * @param sessionId the ID of the session to end.
     * @param username  the username of the user requesting the termination.
     * @throws SecurityException if the user is not a member of the session.
     */
    @Transactional
    public void endSessionByUsers(String sessionId, String username) {
        Session session = sessionRepo.findById(sessionId).orElseThrow();

        if (!session.getUsers().toString().contains(username)) {
            throw new SecurityException(username + "user not Authorized");
        }

        List<String> keysToDelete = session.getFiles().stream().map(Files::getR2Key).toList();

        if (!keysToDelete.isEmpty()) {
            try {
                r2Service.deleteFiles(keysToDelete);
                System.out.println("R2 files for session " + sessionId + " deleted.");
            } catch (Exception e) {
                throw new RuntimeException(
                        "Failed to delete R2 files for session " + sessionId + ". Aborting DB deletion.", e);
            }
        }
        sessionRepo.delete(session);
        notiffService.sessionClosedNotiff(sessionId, username);
    }

    /**
     * Removes a user from a session and broadcasts a 'USER_LEFT' notification.
     *
     * @param sessionId the ID of the session.
     * @param username  the display name of the user to remove.
     * @throws NoSuchElementException if the user is not found in the session.
     */
    @Transactional
    public void removeUser(String sessionId, String username) {
        Optional<Users> user = userRepo.findByUsernameAndSessionSessionId(username, sessionId);

        if (user.isEmpty())
            throw new NoSuchElementException(username);

        Users userToRemove = user.get();

        userRepo.delete(userToRemove);

        SessionNotiff leaveNotiff = new SessionNotiff(SessionNotiff.NotifyType.USER_LEFT, sessionId, username);

        notiffService.notifySessionMembers(sessionId, leaveNotiff);
    }

    /**
     * Deletes a single file from R2 and the database, then broadcasts
     * a 'FILE_DELETED' notification to all session subscribers.
     *
     * @param sessionId the ID of the session.
     * @param fileName  the original filename of the file to delete.
     * @throws NoSuchElementException if the file is not found.
     */
    public void deleteFile(String sessionId, String fileName) {
        Files file = filesRepo.findByFileNameAndSessionSessionId(fileName, sessionId)
                .orElseThrow(() -> new NoSuchElementException("File " + fileName + " does not exist"));

        String r2Key = file.getR2Key();

        try {
            r2Service.deleteFile(r2Key);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete file " + fileName);
        }
        filesRepo.delete(file);
        SessionNotiff deletionNotiff = new SessionNotiff(SessionNotiff.NotifyType.FILE_DELETED, sessionId,
                file.getFileName());
        notiffService.notifySessionMembers(sessionId, deletionNotiff);
    }

    /**
     * Scheduled cron job that runs every 5 minutes to clean up dead sessions.
     * It sweeps the database for sessions that have either naturally expired via
     * their duration
     * or have been abandoned (no users) for over 2 minutes.
     * Safely deletes all associated files from R2 before wiping the database
     * records.
     */
    @Scheduled(cron = "* 5 * * * *")
    @Transactional
    public void cleanupExpiredSessions() {

        if (sessionRepo.findAll().isEmpty()) {
            return;
        }

        var now = Instant.now();

        List<Session> expiredByTime = sessionRepo.findByExpiresAtBefore(now);

        Instant graceTime = now.minus(2, ChronoUnit.MINUTES).atZone(ZoneOffset.systemDefault())
                .withZoneSameInstant(ZoneId.of("UTC")).toInstant();
        List<Session> expiredByEmpty = sessionRepo.findAbandonedSessions(graceTime);

        Set<Session> sessionsToDelete = new HashSet<>(expiredByTime);
        sessionsToDelete.addAll(expiredByEmpty);

        if (sessionsToDelete.isEmpty()) {
            return;
        }

        Collection<String> keysToDelete = sessionsToDelete.stream().flatMap(session -> session.getFiles().stream())
                .map(Files::getR2Key).toList();

        try {
            if (!keysToDelete.isEmpty())
                r2Service.deleteFiles(keysToDelete);

            sessionRepo.deleteAll(sessionsToDelete);
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to complete session cleanup transaction. R2 or DB deletion failed: "
                    + e.getMessage());
            throw new RuntimeException("Session cleanup failed. Transaction rolled back.", e);
        }
    }
}
