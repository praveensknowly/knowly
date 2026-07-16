package com.knowly.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.knowly.entity.User;
import com.knowly.service.UserService;

@Controller
public class OAuthSetPasswordController {

    private final UserService userService;

    public OAuthSetPasswordController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/oauth2/set-password")
    public String showForm(Authentication auth, Model model) {
        User user = userService.findByEmail(auth.getName());
        if (user.getPassword() != null) {
            return "redirect:/home"; // already set — don't show this again
        }
        model.addAttribute("user", user);
        return "OAuthSetPassword";
    }

    @PostMapping("/oauth2/set-password")
    public String submit(@RequestParam(required = false) String newPassword,
                          @RequestParam(required = false) String confirmPassword,
                          Authentication auth, Model model) {
        try {
            userService.setInitialPassword(auth.getName(), newPassword, confirmPassword);
            return "redirect:/home";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", userService.findByEmail(auth.getName()));
            return "OAuthSetPassword";
        }
    }
}
