package com.knowly.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TurnCredentialService {

    @Value("${turn.url:}")
    private String turnUrl;

    @Value("${turn.username:}")
    private String turnUsername;

    @Value("${turn.credential:}")
    private String turnCredential;

    public TurnCredentials generate(String userId) {
        if (turnUrl == null || turnUrl.isBlank() ||
            turnUsername == null || turnUsername.isBlank() ||
            turnCredential == null || turnCredential.isBlank()) {
            return new TurnCredentials("", "", "");
        }
        return new TurnCredentials(turnUrl, turnUsername, turnCredential);
    }

    public record TurnCredentials(String url, String username, String credential) {}
}
