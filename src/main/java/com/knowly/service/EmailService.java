package com.knowly.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendVerificationEmail(String toEmail, String code) {
        String url = "https://api.resend.com/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        String htmlBody = "<p>Welcome to Knowly!</p>"
                + "<p>Your verification code is: <strong>" + code + "</strong></p>"
                + "<p>Enter this code on the signup page to verify your email. "
                + "This code expires in 10 minutes.</p>";

        Map<String, Object> body = new HashMap<>();
        body.put("from", "Knowly <otp@praveensknowly.in>");
        body.put("to", List.of(toEmail));
        body.put("subject", "Your Knowly verification code");
        body.put("html", htmlBody);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to send email via Resend", e);
        }
    }
}