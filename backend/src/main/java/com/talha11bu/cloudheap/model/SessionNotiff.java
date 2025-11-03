package com.talha11bu.cloudheap.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SessionNotiff {
    public enum NotificationType { USER_JOINED, USER_LEFT, FILE_UPLOADED, FILE_DELETED }

    private NotificationType type;
    private String sessionId;
    private String payload;
}
