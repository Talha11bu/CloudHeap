package com.talha11bu.cloudheap.repo;

import com.talha11bu.cloudheap.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface SessionRepo extends JpaRepository<Session, String> {
    List<Session> findByExpiresAt(LocalTime expiresAt);
}
