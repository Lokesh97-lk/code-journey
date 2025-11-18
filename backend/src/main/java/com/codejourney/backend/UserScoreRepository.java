package com.codejourney.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserScoreRepository extends JpaRepository<UserScore, Long> {
    // Finds activity history, newest first
    List<UserScore> findByUserIdOrderByCompletedAtDesc(Long userId);
}