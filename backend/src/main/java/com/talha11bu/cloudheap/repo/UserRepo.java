package com.talha11bu.cloudheap.repo;

import com.talha11bu.cloudheap.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {

    List<Users> findBySessionSessionId(String sessionId);
}