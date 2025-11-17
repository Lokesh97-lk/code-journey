package com.codejourney.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserScoreRepository extends JpaRepository<UserScore, Long> {

    // Finds all scores for a specific user, ordered by the newest first
    List<UserScore> findByUserIdOrderByCompletedAtDesc(Long userId);
}