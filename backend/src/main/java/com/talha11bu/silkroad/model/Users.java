package com.talha11bu.silkroad.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "A user participating in a session")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Auto-generated primary key", example = "1")
    private int id; // A primary key for the USERS table itself

    @Schema(description = "JWT token assigned to this user", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "WebSocket session ID for real-time notifications", example = "ws-session-abc123")
    private String webSocketId;

    @Schema(description = "Display name of the user", example = "alice")
    private String username;

    @ToString.Exclude
    @JsonIgnoreProperties("users")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SessionID", nullable = false)
    @Schema(hidden = true)
    private Session session;

    public Users(String username, String token, String webSocketId, Session session) {
        this.username = username;
        this.token = token;
        this.webSocketId = webSocketId;
        this.session = session;
    }
}
