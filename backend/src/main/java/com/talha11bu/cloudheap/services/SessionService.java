package com.talha11bu.cloudheap.services;

import com.talha11bu.cloudheap.model.CreateRequest;
import com.talha11bu.cloudheap.model.CreateResponse;
import com.talha11bu.cloudheap.repo.SessionRepo;
import com.talha11bu.cloudheap.repo.UserRepo;
import com.talha11bu.cloudheap.model.Session;
import com.talha11bu.cloudheap.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

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
            LocalTime duration = LocalTime.now().plusMinutes(request.duration());

            Session newSession = new Session(newSessionId, request.password(), duration);
            Session savedSession = sessionRepo.save(newSession);
            Users initialUser = new Users(request.username(), savedSession);

            userRepo.save(initialUser);
            return new CreateResponse(true, savedSession.getSessionId(), savedSession.getExpiresAt());
        }catch (Exception e){
            return new CreateResponse(false, null, null);
        }
    }

}
