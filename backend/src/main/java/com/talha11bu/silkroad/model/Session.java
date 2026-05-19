package com.talha11bu.silkroad.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


/**
 * JPA entity representing an ephemeral file-sharing session.
 *
 * <p>A session is the core domain object in SilkRoad. It is identified by a short,
 * human-readable ID (e.g., {@code SKR-7X9K2M}), protected by a password, and
 * automatically expires after a configurable duration. It maintains bidirectional
 * relationships with its {@link Users} and {@link Files}.</p>
 *
 * <p>Cascade deletion ensures that when a session is removed, all associated
 * users and files are cleaned up from the database.</p>
 */
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

    /**
     * Creates a new session with the specified ID, password, and expiration timestamp.
     *
     * @param sessionId the unique session identifier.
     * @param password  the password protecting the session.
     * @param expiresAt the timestamp at which the session should expire.
     */
    public Session(String sessionId, String password, Instant expiresAt) {
        this.sessionId = sessionId;
        this.password = password;
        this.expiresAt = expiresAt;
    }

    /**
     * JPA lifecycle callback that automatically sets the creation timestamp
     * before the entity is first persisted.
     */
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

    /**
     * Checks whether this session has passed its expiration time.
     *
     * @return {@code true} if the current time is after {@link #expiresAt}.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }
}
