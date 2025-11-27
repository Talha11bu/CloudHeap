package com.talha11bu.silkroad.repo;

import com.talha11bu.silkroad.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepo extends JpaRepository<Session, String> {
    List<Session> findByExpiresAtBefore(LocalDateTime expiresAt);

    @Query("SELECT s FROM Session s LEFT JOIN FETCH s.files f LEFT JOIN s.users u WHERE u.id IS NULL")
    List<Session> findEmptySessions();
}
