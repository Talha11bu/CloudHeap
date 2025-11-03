package com.talha11bu.cloudheap.model;

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
public class Files {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fileName;

    private long size;

    private String contentType;

    @ToString.Exclude
    @JsonIgnoreProperties("files")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SessionID", nullable = false)
    private Session session;

    public Files(String fileName, long size, String contentType, Session session) {
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.session = session;
    }

}
