package com.talha11bu.cloudheap.services;

import com.talha11bu.cloudheap.model.*;
import com.talha11bu.cloudheap.repo.SessionRepo;
import com.talha11bu.cloudheap.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class SessionService {

    @Autowired
    private SessionRepo sessionRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SessionIdGenerator idGenerator;

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
}
