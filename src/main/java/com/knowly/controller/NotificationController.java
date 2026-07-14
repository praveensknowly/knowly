package com.knowly.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.knowly.dto.HelpingSessionDto;
import com.knowly.dto.LearningSessionDto;
import com.knowly.service.HelpSessionService;

@Controller
public class NotificationController {
    private final HelpSessionService helpSessionService;

    public NotificationController(HelpSessionService helpSessionService) {
        this.helpSessionService = helpSessionService;
    }

    @GetMapping("/notifications")
    public String notifications(Authentication auth, Model model) {
        String email = auth.getName();

        // Fetch received requests (helper side) - filter to pending
        List<HelpingSessionDto> allReceived = helpSessionService.findForHelper(email);
        List<HelpingSessionDto> receivedRequests = allReceived.stream()
                .filter(s -> "pending".equals(s.getTab()))
                .sorted(Comparator.comparing(HelpingSessionDto::getTimeAgo).reversed()) // oldest first
                .toList();

        // Fetch sent requests (requester side) - filter to pending
        List<LearningSessionDto> allSent = helpSessionService.findForRequester(email);
        List<LearningSessionDto> sentRequests = allSent.stream()
                .filter(s -> "pending".equals(s.getTab()))
                .sorted(Comparator.comparing(LearningSessionDto::getTimeAgo).reversed()) // oldest first
                .toList();

        model.addAttribute("receivedRequests", receivedRequests);
        model.addAttribute("sentRequests", sentRequests);

        return "Notifications";
    }
}
