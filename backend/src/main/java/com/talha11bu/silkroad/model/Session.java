package com.talha11bu.silkroad.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Session {

    @Id
    private String sessionId;

    private String password;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @ToString.Exclude
    @JsonIgnoreProperties("session")
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Users> users = new ArrayList<>();

    @ToString.Exclude
    @JsonIgnoreProperties("session")
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Files> files = new ArrayList<>();

    public Session() {}

    public Session(String sessionId, String password, Instant expiresAt) {
        this.sessionId = sessionId;
        this.password = password;
        this.expiresAt = expiresAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
    public String getPassword(){
        return password;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Users> getUsers() {
        return users;
    }

    public List<Files> getFiles() {
        return files;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }
}

