package com.talha11bu.silkroad.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * WebSocket notification payload broadcast to all session subscribers.
 *
 * <p>Sent over STOMP to {@code /topic/session/{sessionId}} whenever a
 * state-changing event occurs (user join/leave, file upload/delete).</p>
 */
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "WebSocket notification payload sent to session subscribers via /topic/sessions/{sessionId}")
public class SessionNotiff {

    /**
     * Enum of event types that can trigger a WebSocket notification.
     */

    @Schema(description = "Type of notification event")
    public enum NotifyType { USER_JOINED, USER_LEFT, FILE_UPLOADED, FILE_DELETED }

    @Schema(description = "The type of event that occurred", example = "USER_JOINED")
    private NotifyType type;

    @Schema(description = "Session ID where the event occurred", example = "SKR-7X9K2M")
    private String sessionId;

    @Schema(description = "Event payload — typically a username or filename", example = "bob")
    private String payload;
}
