package com.knowly.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knowly.entity.EmailVerificationToken;
import com.knowly.repository.EmailVerificationTokenRepository;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepo;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public EmailVerificationService(EmailVerificationTokenRepository tokenRepo,
                                     EmailService emailService) {
        this.tokenRepo = tokenRepo;
        this.emailService = emailService;
    }

    @Transactional
    public void sendOtp(String email) {
        tokenRepo.deleteByEmail(email);

        String code = String.format("%06d", random.nextInt(1_000_000));

        EmailVerificationToken token = new EmailVerificationToken();
        token.setEmail(email);
        token.setToken(code);
        token.setVerified(false);
        token.setAttempts(0);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        tokenRepo.save(token);

        emailService.sendVerificationEmail(email, code);
    }

    @Transactional
    public boolean verifyOtp(String email, String code) {
        var tokenOpt = tokenRepo.findByEmailAndToken(email, code);
        if (tokenOpt.isEmpty()) {
            return false; // wrong code
        }

        EmailVerificationToken token = tokenOpt.get();
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false; // expired
        }

        if (token.getAttempts() >= 5) {
            return false; // too many attempts
        }

        token.setAttempts(token.getAttempts() + 1);
        token.setVerified(true);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(30)); // grace window to finish signup
        tokenRepo.save(token);

        return true;
    }

    @Transactional
    public boolean consumeVerifiedEmail(String email) {
        var tokenOpt = tokenRepo.findByEmailAndVerifiedTrue(email);
        if (tokenOpt.isEmpty()) {
            return false;
        }

        EmailVerificationToken token = tokenOpt.get();
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false; // they verified too long ago, window expired
        }

        tokenRepo.delete(token); // one-time use — consumed by signup now
        return true;
    }
}