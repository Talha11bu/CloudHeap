package com.talha11bu.silkroad.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // A primary key for the USERS table itself

    private String token;

    private String webSocketId;

    private String username;

    @ToString.Exclude
    @JsonIgnoreProperties("users")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SessionID", nullable = false)
    private Session session;

    public Users(String username, String token, String webSocketId, Session session) {
        this.username = username;
        this.token = token;
        this.webSocketId = webSocketId;
        this.session = session;
    }
}
