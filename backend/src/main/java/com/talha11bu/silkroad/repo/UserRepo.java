package com.talha11bu.silkroad.repo;

import com.talha11bu.silkroad.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Users} entities.
 *
 * <p>Provides CRUD operations and derived queries for looking up
 * users within a specific session context.</p>
 */
@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {

    /**
     * Finds all users belonging to a specific session.
     *
     * @param sessionId the ID of the session.
     * @return a list of users in the session.
     */
    List<Users> findBySessionSessionId(String sessionId);

    /**
     * Finds a specific user by their username within a session.
     * Used for leave/disconnect operations.
     *
     * @param username  the user's display name.
     * @param sessionId the ID of the session.
     * @return an {@link Optional} containing the user if found.
     */
    Optional<Users> findByUsernameAndSessionSessionId(String username, String sessionId);
}