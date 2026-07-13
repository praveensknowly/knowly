package com.knowly.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your Knowly verification code");
        message.setText("Welcome to Knowly!\n\nYour verification code is: " + code
                + "\n\nEnter this code on the signup page to verify your email. "
                + "This code expires in 10 minutes.");
        mailSender.send(message);
    }
}