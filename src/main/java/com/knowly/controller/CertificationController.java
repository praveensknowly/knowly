package com.knowly.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.knowly.dto.CertificationDto;
import com.knowly.service.CertificationService;
import com.knowly.service.UserService;

@Controller
public class CertificationController {

	private static final Logger logger = LoggerFactory.getLogger(CertificationController.class);

    private final CertificationService certificationService;
    private final UserService userService;

    public CertificationController(CertificationService certificationService, UserService userService) {
        this.certificationService = certificationService;
        this.userService = userService;
    }

    @GetMapping("/profile/edit/certifications")
    public String showCertificationsPage(Authentication auth, Model model) {
        model.addAttribute("profile", userService.getProfile(auth.getName()));
        model.addAttribute("certificationsList", certificationService.findAll(auth.getName()));
        model.addAttribute("certification", new CertificationDto());
        return "Certificationedit";
    }

    @PostMapping("/profile/edit/certifications")
    public String saveCertification(CertificationDto dto, Authentication auth, Model model) {
        try {
            certificationService.save(dto, auth.getName());
            return "redirect:/profile/edit/certifications";
        } catch (Exception e) {
            logger.error("Failed to save certification for user {}", auth.getName(), e);
            model.addAttribute("message", e.getMessage());
            model.addAttribute("certificationsList", certificationService.findAll(auth.getName()));
            model.addAttribute("certification", dto);
            return "Certificationedit";
        }
    }

    @PostMapping("/profile/edit/certifications/delete")
    public String deleteCertification(String certificationId, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            certificationService.deleteById(certificationId, auth.getName());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Unable to delete. Please try again.");
        }
        return "redirect:/profile/edit/certifications";
    }
}
