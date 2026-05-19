package com.talha11bu.silkroad.repo;

import com.talha11bu.silkroad.model.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Files} entities.
 *
 * <p>Provides CRUD operations and custom queries for file metadata
 * stored within sessions.</p>
 */
@Repository
public interface FilesRepo extends JpaRepository<Files, Integer> {

    /**
     * Finds a file by its original filename within a specific session.
     *
     * @param filename  the original name of the file.
     * @param sessionId the ID of the parent session.
     * @return an {@link Optional} containing the file if found.
     */
    Optional<Files> findByFileNameAndSessionSessionId(String filename, String sessionId);
}
