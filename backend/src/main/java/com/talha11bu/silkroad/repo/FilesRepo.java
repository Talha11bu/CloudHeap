package com.talha11bu.silkroad.repo;

import com.talha11bu.silkroad.model.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilesRepo extends JpaRepository<Files, Integer> {
    Optional<Files> findByFileNameAndSessionSessionId(String filename, String sessionId);
}
