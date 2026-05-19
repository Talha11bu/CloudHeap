package com.talha11bu.silkroad.repo;

import com.talha11bu.silkroad.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Session} entities.
 *
 * <p>Provides CRUD operations and custom JPQL queries used by the
 * scheduled cleanup job to identify expired or abandoned sessions.</p>
 */
@Repository
public interface SessionRepo extends JpaRepository<Session, String> {

    /**
     * Finds all sessions whose expiration timestamp is before the given instant.
     *
     * @param expiresAt the cutoff instant.
     * @return a list of sessions that have expired.
     */
    List<Session> findByExpiresAtBefore(Instant expiresAt);

    /**
     * Finds sessions that have no users and eagerly fetches their files.
     * Used to identify orphaned sessions for cleanup.
     *
     * @return a list of sessions with zero users.
     */
    @Query("SELECT s FROM Session s LEFT JOIN FETCH s.files f LEFT JOIN s.users u WHERE u.id IS NULL")
    List<Session> findEmptySessions();

    /**
     * Finds sessions that have no users and were created before the given grace time.
     * Prevents premature deletion of sessions where the creator hasn't connected yet.
     *
     * @param graceTime the cutoff timestamp — sessions created before this are eligible.
     * @return a list of abandoned sessions.
     */
    @Query("SELECT s FROM Session s WHERE s.users IS EMPTY AND s.createdAt < :graceTime")
    List<Session> findAbandonedSessions(@Param("graceTime") Instant graceTime);
}
