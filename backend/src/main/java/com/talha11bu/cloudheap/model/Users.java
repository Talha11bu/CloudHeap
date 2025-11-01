package com.talha11bu.cloudheap.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // A primary key for the USERS table itself

    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SessionID", nullable = false)
    private Session session;

    public Users(String username, Session session) {
        this.username = username;
        this.session = session;
    }
}
