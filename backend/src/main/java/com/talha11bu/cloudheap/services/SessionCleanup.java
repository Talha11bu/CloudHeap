package com.talha11bu.cloudheap.services;

import com.talha11bu.cloudheap.model.Session;
import com.talha11bu.cloudheap.repo.SessionRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionCleanup {

    @Autowired
    private SessionRepo sessionRepo;


    @Scheduled(cron = "0 * * * * *")//runs every minute
    @Transactional
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();

        List<Session> expiredSessions = sessionRepo.findByExpiresAtBefore(now);

        if (!expiredSessions.isEmpty()) {
            // Deletes Sessions. CascadeType.ALL deletes Users and Files automatically.
            sessionRepo.deleteAll(expiredSessions);
            System.out.println("[" + LocalDateTime.now() + "] Deleted " + expiredSessions.size() + " expired sessions.");
        }
    }
}
