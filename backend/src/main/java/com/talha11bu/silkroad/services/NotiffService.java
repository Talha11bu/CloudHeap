package com.talha11bu.silkroad.services;

import com.talha11bu.silkroad.model.SessionNotiff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for broadcasting real-time WebSocket notifications.
 *
 * <p>Uses Spring's {@link SimpMessagingTemplate} to push {@link SessionNotiff}
 * payloads to all clients subscribed to a session's STOMP topic
 * ({@code /topic/session/{sessionId}}).</p>
 */
@Service
public class NotiffService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcasts a notification to all users subscribed to a specific session's topic.
     * Used to push real-time updates like user joins, leaves, and file uploads.
     *
     * @param sessionId    The active session ID.
     * @param notification The payload object containing the event type and relevant metadata.
     */
    public void notifySessionMembers(String sessionId, SessionNotiff  notification) {
        String destination = "/topic/session/" + sessionId;
        messagingTemplate.convertAndSend(destination, notification);
        System.out.println("Broadcasted to " + destination + ": " + notification.getType());
    }

    /**
     * Broadcasts a plain-text closure message to all session subscribers.
     * Sent when a user manually ends the session, informing clients that
     * all associated files have been deleted.
     *
     * @param sessionId the ID of the session that was closed.
     * @param username  the username of the user who ended the session.
     */
    public void sessionClosedNotiff(String sessionId, String username) {
        String payload = String.format("Session %s has been successfully ended. All associated files have been deleted.", sessionId);
        String destination = "/topic/session/" + sessionId;
        messagingTemplate.convertAndSend(destination, payload);

    }
}