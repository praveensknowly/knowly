package com.knowly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knowly.entity.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByEmailAndToken(String email, String token);
    Optional<EmailVerificationToken> findByEmailAndVerifiedTrue(String email);
    void deleteByEmail(String email);
}