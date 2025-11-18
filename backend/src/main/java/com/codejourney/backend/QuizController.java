package com.codejourney.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    // Submit "Practice Lab" Activity
    @PostMapping("/activity/submit")
    public ResponseEntity<?> submitActivity(@RequestBody Map<String, String> payload, Principal principal) {
        
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String topic = payload.getOrDefault("topic", "General Practice");
        String userCode = payload.getOrDefault("code", ""); // Code is saved but not executed server-side

        UserScore activityLog = new UserScore();
        activityLog.setUser(currentUser);
        activityLog.setQuizName(topic); 
        activityLog.setScore(100); // Mark as completed
        activityLog.setMaxScore(100);
        
        userScoreRepository.save(activityLog);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Activity logged successfully!");
        result.put("topic", topic);
        return ResponseEntity.ok(result);
    }

    // Get Activity History
    @GetMapping("/progress")
    public ResponseEntity<List<UserScore>> getProgress(Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<UserScore> history = userScoreRepository.findByUserIdOrderByCompletedAtDesc(currentUser.getId());
        return ResponseEntity.ok(history);
    }
}