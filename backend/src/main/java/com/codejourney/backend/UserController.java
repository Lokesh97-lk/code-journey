package com.codejourney.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> registrationRequest) {
        String email = registrationRequest.get("email");
        String password = registrationRequest.get("password");

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required."));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Email is already registered."));
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));

        // Auto-admin Logic
        if ("lokesh972007@gmail.com".equalsIgnoreCase(email)) {
            newUser.setAdmin(true);
        }

        userRepository.save(newUser);
        return ResponseEntity.ok(Map.of("message", "User registered successfully."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOptional.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password."));
        }

        User user = userOptional.get();
        return ResponseEntity.ok(Map.of(
                "message", "Login successful!",
                "email", user.getEmail(),
                "isAdmin", user.isAdmin()
        ));
    }
}