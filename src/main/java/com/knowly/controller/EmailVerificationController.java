package com.knowly.controller;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import com.knowly.repository.UserRepository;
import com.knowly.service.EmailVerificationService;

@RestController
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final UserRepository userRepo;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public EmailVerificationController(EmailVerificationService emailVerificationService,
                                        UserRepository userRepo) {
        this.emailVerificationService = emailVerificationService;
        this.userRepo = userRepo;
    }

    private Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, k -> Bucket.builder()
            .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(15))))
            .build());
    }

    @PostMapping("/send-otp")
    public Map<String, Object> sendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isBlank()) {
            return Map.of("success", false, "message", "Email is required.");
        }

        if (!resolveBucket(email).tryConsume(1)) {
            return Map.of("success", false, "message", "Too many requests. Please try again later.");
        }

        if (userRepo.existsByEmail(email)) {
            return Map.of("success", false, "message", "An account with this email already exists.");
        }

        emailVerificationService.sendOtp(email);
        return Map.of("success", true, "message", "Code sent to your email.");
    }

    @PostMapping("/verify-otp")
    public Map<String, Object> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");

        if (email == null || email.isBlank()) {
            return Map.of("success", false, "message", "Email is required.");
        }

        if (!resolveBucket(email).tryConsume(1)) {
            return Map.of("success", false, "message", "Too many requests. Please try again later.");
        }

        boolean success = emailVerificationService.verifyOtp(email, code);
        return Map.of(
            "success", success,
            "message", success ? "Email verified." : "Invalid or expired code."
        );
    }
}