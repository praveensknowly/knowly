package com.knowly.controller;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import com.knowly.repository.UserRepository;
import com.knowly.service.EmailVerificationService;
import com.knowly.service.UserService;

@Controller
public class ForgotPasswordController {

    private final UserRepository userRepo;
    private final EmailVerificationService emailVerificationService;
    private final UserService userService;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public ForgotPasswordController(UserRepository userRepo,
                                     EmailVerificationService emailVerificationService,
                                     UserService userService) {
        this.userRepo = userRepo;
        this.emailVerificationService = emailVerificationService;
        this.userService = userService;
    }

    private Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, k -> Bucket.builder()
            .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(15))))
            .build());
    }

    @GetMapping("/forgot-password")
    public String showForm() {
        return "ForgotPassword";
    }

    @PostMapping("/forgot-password/send-otp")
    @ResponseBody
    public Map<String, Object> sendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return Map.of("success", false, "message", "Email is required.");
        }
        if (!resolveBucket(email).tryConsume(1)) {
            return Map.of("success", false, "message", "Too many requests. Please try again later.");
        }

        var userOpt = userRepo.findByEmail(email);
        // Don't reveal account existence either way — always report success
        if (userOpt.isPresent() && "LOCAL".equals(userOpt.get().getProvider())) {
            emailVerificationService.sendOtp(email);
        }
        return Map.of("success", true, "message", "If an account exists for that email, a code has been sent.");
    }

    @PostMapping("/forgot-password/reset")
    @ResponseBody
    public Map<String, Object> reset(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        String newPassword = body.get("newPassword");

        if (email == null || code == null || newPassword == null) {
            return Map.of("success", false, "message", "All fields are required.");
        }
        if (!resolveBucket(email + ":reset").tryConsume(1)) {
            return Map.of("success", false, "message", "Too many requests. Please try again later.");
        }
        if (newPassword.length() < 6) {
            return Map.of("success", false, "message", "Password must be at least 6 characters long.");
        }
        if (!emailVerificationService.verifyOtp(email, code)) {
            return Map.of("success", false, "message", "Invalid or expired code.");
        }
        if (!emailVerificationService.consumeVerifiedEmail(email)) {
            return Map.of("success", false, "message", "Verification expired — please request a new code.");
        }
        if (!userService.resetPassword(email, newPassword)) {
            return Map.of("success", false, "message", "Unable to reset password for this account.");
        }
        return Map.of("success", true, "message", "Password updated. You can now log in.");
    }
}
