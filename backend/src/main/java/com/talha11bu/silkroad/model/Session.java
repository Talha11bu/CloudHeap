package com.talha11bu.silkroad.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Schema(description = "A temporary file-sharing session with password protection and auto-expiration")
public class Session {

    @Id
    @Schema(description = "Unique session identifier", example = "SKR-7X9K2M")
    private String sessionId;

    @Schema(description = "Password protecting the session", example = "mySecret123")
    private String password;

    @Column(nullable = false, updatable = false)
    @Schema(description = "Timestamp when the session was created", example = "2025-06-15T13:30:00Z")
    private Instant createdAt;

    @Column(nullable = false)
    @Schema(description = "Timestamp when the session expires", example = "2025-06-15T14:30:00Z")
    private Instant expiresAt;

    @ToString.Exclude
    @JsonIgnoreProperties("session")
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "List of users currently in the session")
    private List<Users> users = new ArrayList<>();

    @ToString.Exclude
    @JsonIgnoreProperties("session")
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "List of files uploaded to the session")
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
