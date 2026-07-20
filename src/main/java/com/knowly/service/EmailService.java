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

    @Value("${resend.from.email}")
    private String resendFromEmail;

    @Value("${contact.support.email}")
    private String supportEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendFeedbackEmail(String fromName, String fromEmail, String message) {
        String url = "https://api.resend.com/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        String htmlBody = "<p>New feedback submitted on Knowly.</p>"
                + "<p><strong>From:</strong> " + escapeHtml(fromName) + " (" + escapeHtml(fromEmail) + ")</p>"
                + "<p><strong>Message:</strong></p>"
                + "<p>" + escapeHtml(message).replace("\n", "<br/>") + "</p>";

        Map<String, Object> body = new HashMap<>();
        body.put("from", "Knowly <" + resendFromEmail + ">");
        body.put("to", List.of(supportEmail));
        body.put("reply_to", fromEmail);
        body.put("subject", "Knowly Feedback from " + fromName);
        body.put("html", htmlBody);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to send feedback email via Resend", e);
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

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
        body.put("from", "Knowly <" + resendFromEmail + ">");
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

    public void sendNotificationEmail(String toEmail, String toName, String title, String body, String url) {
        String apiUrl = "https://api.resend.com/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        String htmlBody = "<p>Hello " + escapeHtml(toName) + ",</p>"
                + "<p><strong>" + escapeHtml(title) + "</strong></p>"
                + "<p>" + escapeHtml(body).replace("\n", "<br/>") + "</p>"
                + "<p><a href=\"" + escapeHtml(url) + "\">View on Knowly</a></p>";

        Map<String, Object> emailBody = new HashMap<>();
        emailBody.put("from", "Knowly <" + resendFromEmail + ">");
        emailBody.put("to", List.of(toEmail));
        emailBody.put("subject", title);
        emailBody.put("html", htmlBody);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailBody, headers);

        try {
            restTemplate.postForEntity(apiUrl, request, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to send notification email via Resend", e);
        }
    }
}