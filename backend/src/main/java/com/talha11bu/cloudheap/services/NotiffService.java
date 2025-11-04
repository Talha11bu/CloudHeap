package com.talha11bu.cloudheap.services;

import com.talha11bu.cloudheap.model.SessionNotiff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotiffService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifySessionMembers(String sessionId, SessionNotiff  notification) {
        String destination = "/topic/session/" + sessionId;
        messagingTemplate.convertAndSend(destination, notification);
        System.out.println("Broadcasted to " + destination + ": " + notification.getType());
    }

    public void sessionClosedNotiff(String sessionId, String username) {
        String payload = String.format("Session %s has been successfully ended. All associated files have been deleted.", sessionId);
        String destination = "/topic/session/" + sessionId;
        messagingTemplate.convertAndSend(destination, payload);

    }
}