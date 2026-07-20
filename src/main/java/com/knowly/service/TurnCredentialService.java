package com.knowly.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TurnCredentialService {

    @Value("${turn.secret:}")
    private String turnSecret;

    @Value("${turn.url:}")
    private String turnUrl;

    public TurnCredentials generate(String userId) {
        if (turnSecret == null || turnSecret.isBlank() || turnUrl == null || turnUrl.isBlank()) {
            return new TurnCredentials("", "", "");
        }
        long expiry = (System.currentTimeMillis() / 1000L) + 3600; // 1 hour
        String username = expiry + ":" + userId;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(turnSecret.getBytes(), "HmacSHA1"));
            String credential = Base64.getEncoder().encodeToString(mac.doFinal(username.getBytes()));
            return new TurnCredentials(turnUrl, username, credential);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate TURN credentials", e);
        }
    }

    public record TurnCredentials(String url, String username, String credential) {}
}
