package com.knowly.config;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.knowly.dto.HelpingSessionDto;
import com.knowly.entity.UserProfile;
import com.knowly.service.HelpSessionService;
import com.knowly.service.UserService;

@ControllerAdvice
public class NavbarModelAdvice {

    private final UserService userService;
    private final HelpSessionService helpSessionService;

    public NavbarModelAdvice(UserService userService, HelpSessionService helpSessionService) {
        this.userService = userService;
        this.helpSessionService = helpSessionService;
    }

    @ModelAttribute("navProfile")
    public UserProfile navProfile(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        try {
            return userService.getProfile(auth.getName());
        } catch (Exception e) {
            return null;
        }
    }

    @ModelAttribute("notificationCount")
    public int notificationCount(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return 0;
        }
        try {
            List<HelpingSessionDto> receivedRequests = helpSessionService.findForHelper(auth.getName());
            return (int) receivedRequests.stream()
                    .filter(s -> "pending".equals(s.getTab()))
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }
}
