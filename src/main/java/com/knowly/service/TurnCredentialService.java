package com.knowly.service;

import java.util.Arrays;
import java.util.List;
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
            return new TurnCredentials(List.of(), "", "");
        }
        List<String> urls = Arrays.asList(turnUrl.split(","));
        return new TurnCredentials(urls, turnUsername, turnCredential);
    }

    public record TurnCredentials(List<String> urls, String username, String credential) {}
}
