package com.codejourney.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QuizController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserScoreRepository userScoreRepository;

    // This is the grading key.
    private static final Map<String, String> PYTHON_ANSWERS = Map.of(
            "q1", "a", // Slicing
            "q2", "b"  // Merging Dictionaries
    );
    private static final int PYTHON_MAX_SCORE = 2;

    @PostMapping("/quiz/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody Map<String, String> answers, @AuthenticationPrincipal UserDetails userDetails) {

        // Find the user who submitted the quiz
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Grade the quiz
        int score = 0;
        for (Map.Entry<String, String> entry : PYTHON_ANSWERS.entrySet()) {
            if (entry.getValue().equals(answers.get(entry.getKey()))) {
                score++;
            }
        }

        // Save the score to the database
        UserScore userScore = new UserScore();
        userScore.setUser(currentUser);
        userScore.setQuizName("Python Basics Quiz");
        userScore.setScore(score);
        userScore.setMaxScore(PYTHON_MAX_SCORE);
        userScoreRepository.save(userScore);

        // Return the result
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Quiz submitted!");
        result.put("score", score);
        result.put("maxScore", PYTHON_MAX_SCORE);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/progress")
    public ResponseEntity<List<UserScore>> getProgress(@AuthenticationPrincipal UserDetails userDetails) {

        // Find the user
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find all their scores and return them
        List<UserScore> scores = userScoreRepository.findByUserIdOrderByCompletedAtDesc(currentUser.getId());
        return ResponseEntity.ok(scores);
    }
}