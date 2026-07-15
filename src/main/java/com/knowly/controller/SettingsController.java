package com.knowly.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.knowly.dto.FeedbackDto;
import com.knowly.entity.User;
import com.knowly.service.EmailService;
import com.knowly.service.UserService;

@Controller
public class SettingsController {

    private final EmailService emailService;
    private final UserService userService;

    @Value("${contact.support.email}")
    private String supportEmail;

    @Value("${contact.support.phone}")
    private String supportPhone;

    public SettingsController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @GetMapping("/settings")
    public String settings(Authentication auth, Model model) {
        model.addAttribute("supportEmail", supportEmail);
        model.addAttribute("supportPhone", supportPhone);
        return "Settings";
    }

    @PostMapping("/settings/feedback")
    @ResponseBody
    public ResponseEntity<String> submitFeedback(@RequestBody FeedbackDto dto, Authentication auth) {
        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body("error: Message cannot be empty.");
        }
        if (dto.getMessage().length() > 2000) {
            return ResponseEntity.badRequest().body("error: Message is too long.");
        }

        User user = userService.findByEmail(auth.getName());
        try {
            emailService.sendFeedbackEmail(user.getName(), user.getEmail(), dto.getMessage().trim());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("error: Failed to send feedback. Please try again.");
        }
        return ResponseEntity.ok("Feedback sent. Thank you!");
    }
}
