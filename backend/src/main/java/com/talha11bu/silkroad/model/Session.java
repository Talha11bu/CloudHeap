package com.talha11bu.silkroad.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    private String sessionId;

    private String password;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @ToString.Exclude
    @JsonIgnoreProperties("session")
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Users> users = new ArrayList<>();

    @ToString.Exclude
    @JsonIgnoreProperties("session")
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Files> files = new ArrayList<>();

    public Session(String sessionId, String password, LocalDateTime expiresAt) {
        this.sessionId = sessionId;
        this.password = password;
        this.expiresAt = expiresAt;
    }

    public String getSessionId() {
        return sessionId;
    }
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public String getPassword(){
        return password;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}

