package com.talha11bu.cloudheap.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Session {

    @Id
    private String sessionId;

    private String password;

    @Column(nullable = false)
    private LocalTime expiresAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Users> users = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Files> files = new ArrayList<>();

    public Session(String sessionId, String password, LocalTime expiresAt) {
        this.sessionId = sessionId;
        this.password = password;
        this.expiresAt = expiresAt;
    }
    public String getSessionId() {
        return sessionId;
    }

    public LocalTime getExpiresAt() {
        return expiresAt;
    }

}

