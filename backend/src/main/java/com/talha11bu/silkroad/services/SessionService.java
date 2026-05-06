package com.talha11bu.silkroad.services;

import com.talha11bu.silkroad.model.*;
import com.talha11bu.silkroad.repo.*;

import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    @Transactional
    public CreateResponse createSession(CreateRequest request) {
        try {
            String newSessionId = idGenerator.generatedId();

            var expiration = Instant.now().plus(request.duration());

            Session newSession = new Session(newSessionId, request.password(), expiration);
            Session savedSession = sessionRepo.save(newSession);
            String token = jwtTokenService.generateToken(savedSession.getSessionId(), request.username());
            Users initialUser = new Users(request.username(), token, "/topic/session/" + savedSession.getSessionId(), savedSession);

            userRepo.save(initialUser);

            return new CreateResponse(true, savedSession.getSessionId(), request.username(),savedSession.getPassword(),
                    token, request.duration());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new CreateResponse(false, null, null, null, null, null);
        }
    }

    @Transactional
    public JoinResponse joinSession(JoinRequest request) {
        try {
            Session session = sessionRepo.findById(request.sessionId()).orElseThrow();
            if (!request.password().equals(session.getPassword())) {
                return new JoinResponse(false, null, null, null);
            }
            if (session.isExpired()) {
                return new JoinResponse(false, null, null, null);
            }

            var token = jwtTokenService.generateToken(request.sessionId(), request.username());
            Users newUsers = new Users(request.username(), token, "/topic/session/" + request.sessionId() ,session);
            userRepo.save(newUsers);

            SessionNotiff joinNotification = new SessionNotiff(SessionNotiff.NotifyType.USER_JOINED,
                    session.getSessionId(), request.username());

            Session responseSession = sessionRepo.findById(request.sessionId()).orElseThrow();

            notiffService.notifySessionMembers(session.getSessionId(), joinNotification);

            Duration timeLeft = Duration.between(Instant.now(), session.getExpiresAt());

            if (timeLeft.isNegative()) {
                return new JoinResponse(false, null, null, null);
            }

            return new JoinResponse(true, token,
                    responseSession, timeLeft);

        } catch (Exception e) {
            return new JoinResponse(false, null, null, null);
        }
    }

    public JoinResponse rejoinSession(String token) {
        Claims claims = jwtTokenService.validateAndParseToken(token);
        String sessionId = claims.get("sid", String.class);
        String name = claims.getSubject();

        try {
            Session session = sessionRepo.findById(sessionId).orElseThrow();
            Duration timeLeft = Duration.between(Instant.now(), session.getExpiresAt());

            return new JoinResponse(true, token, session, timeLeft);
        } catch (Exception e) {
            return new JoinResponse(false, null, null, null);
        }
    }

    @Transactional
    public UploadResponse uploadFile(String sessionId, MultipartFile multipartFile) throws IOException {
        Session session = sessionRepo.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found."));
        try {
            String r2Key = r2Service.uploadFile(multipartFile, sessionId);

            Files newFileEntity = new Files(multipartFile.getOriginalFilename(), r2Key, session);

            filesRepo.save(newFileEntity);
        } catch (IOException e) {
            throw new RuntimeException("R2 upload failed " + e.getMessage());
        }

        SessionNotiff fileNotification = new SessionNotiff(SessionNotiff.NotifyType.FILE_UPLOADED, sessionId,
                multipartFile.getOriginalFilename());
        notiffService.notifySessionMembers(sessionId, fileNotification);

        return new UploadResponse(multipartFile.getOriginalFilename(), multipartFile.getContentType(),
                multipartFile.getSize());
    }

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

    // Inside SessionService.java

    public String getPreSignedUrlForFile(String sessionId, String fileName, String token) {
        // 1. Verify the user is authenticated for this specific session
        Claims claims = jwtTokenService.validateAndParseToken(token);
        String tokenSessionId = claims.get("sid", String.class);

        if (!tokenSessionId.equals(sessionId)) {
            throw new SecurityException("You do not have access to this session's files.");
        }
        try {
            Files file = filesRepo.findByFileNameAndSessionSessionId(fileName, sessionId).orElseThrow();

            return r2Service.generatePreSignedDownloadUrl(file.getR2Key());
        } catch (Exception e) {
            return null;
        }
    }

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

    @Scheduled(cron = "* 5 * * * *") // runs every 5 minutes
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