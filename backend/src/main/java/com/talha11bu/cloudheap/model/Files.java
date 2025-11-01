package com.talha11bu.cloudheap.model;

import jakarta.persistence.*;

@Entity
public class Files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SessionID", nullable = false)
    private Session session;

    public Files(String fileName, Session session) {
        this.fileName = fileName;
        this.session = session;
    }

}
