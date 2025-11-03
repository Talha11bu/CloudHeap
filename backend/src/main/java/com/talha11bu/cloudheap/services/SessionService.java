package com.talha11bu.cloudheap.services;

import com.talha11bu.cloudheap.model.*;
import com.talha11bu.cloudheap.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
    private NotiffService notiffService;
    @Autowired
    private R2Service r2Service;

    @Transactional
    public CreateResponse createSession(CreateRequest request){
        try{
            String newSessionId = idGenerator.generatedId();
            String username = request.username();
            String password = request.password();
            LocalDateTime timeStamp = LocalDateTime.now().plusMinutes(request.duration());

            Session newSession = new Session(newSessionId, request.password(), timeStamp);
            Session savedSession = sessionRepo.save(newSession);
            Users initialUser = new Users(request.username(), savedSession);

            userRepo.save(initialUser);

            Duration duration  = Duration.between(LocalDateTime.now(), savedSession.getExpiresAt());
            return new CreateResponse(true, savedSession.getSessionId(), savedSession.getPassword(), duration);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new CreateResponse(false, null, null, null);
        }
    }

    @Transactional
    public JoinResponse joinSession(JoinRequest request) {
        try {
            Session session = sessionRepo.findById(request.sessionId()).orElseThrow();
            if(!request.password().equals(session.getPassword())){
                return new JoinResponse(false, null, null, "Invalid Password");
            }
            if (session.isExpired()) {
                return new JoinResponse(false, null, null, "Session Expired");
            }

            Users newUsers = new Users(request.username(), session);
            userRepo.save(newUsers);

            SessionNotiff joinNotification = new SessionNotiff(
                    SessionNotiff.NotificationType.USER_JOINED,
                    session.getSessionId(),
                    request.username()
            );
            notiffService.notifySessionMembers(session.getSessionId(), joinNotification);

            Duration timeLeft = Duration.between(LocalDateTime.now(), session.getExpiresAt());

            Session responseSession  = sessionRepo.findById(request.sessionId()).get();

            return new JoinResponse(
                    true,
                    responseSession,
                    timeLeft,
                    "Joined Successfully"
            );

        } catch (Exception e) {
            return new JoinResponse(false, null, null, "Session Does not Exist");
        }
    }

    @Transactional
    public UploadResponse uploadFile(String sessionId, MultipartFile multipartFile) throws IOException {
        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found."));

        if (session.isExpired()) {
            throw new RuntimeException("Session has expired.");
        }

        String r2Key = r2Service.uploadFile(multipartFile, sessionId);

        Files newFileEntity = new Files(r2Key, session);
        filesRepo.save(newFileEntity);

        SessionNotiff fileNotification = new SessionNotiff(
                SessionNotiff.NotificationType.FILE_UPLOADED,
                sessionId,
                multipartFile.getOriginalFilename() // Send the user-friendly original name
        );
        notiffService.notifySessionMembers(sessionId, fileNotification);

        return new UploadResponse(multipartFile.getOriginalFilename(), multipartFile.getContentType(), multipartFile.getSize());
    }

    @Scheduled(cron = "0 * * * * *")//runs every minute
    @Transactional
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();

        List<Session> expiredSessions = sessionRepo.findByExpiresAtBefore(now);

        if (expiredSessions.isEmpty()) {
            return;
        }

        Collection<String> keysToDelete = expiredSessions.stream()
                .flatMap(session -> session.getFiles().stream())
                .map(Files::getFileName)
                .toList();

        try{
            r2Service.deleteFiles(keysToDelete);
            sessionRepo.deleteAll(expiredSessions);
        }catch (Exception e){
            System.err.println("CRITICAL: Failed to complete session cleanup transaction. R2 or DB deletion failed: " + e.getMessage());
            throw new RuntimeException("Session cleanup failed. Transaction rolled back.", e);
        }
    }
}
