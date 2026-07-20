package com.knowly.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowly.model.UserPrinciple;
import com.knowly.service.UserService;

@RestController
public class PresenceController {

    private final UserService userService;

    public PresenceController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/heartbeat")
    public void heartbeat(@AuthenticationPrincipal UserPrinciple principal) {
        if (principal == null) return;
        var profile = userService.getProfile(principal.getUsername());
        userService.touchLastActive(profile.getId());
    }
}
