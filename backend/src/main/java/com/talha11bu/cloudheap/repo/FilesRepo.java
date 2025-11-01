package com.talha11bu.cloudheap.repo;

import com.talha11bu.cloudheap.model.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepo extends JpaRepository<Files, Integer> {

}
