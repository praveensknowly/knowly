package com.knowly.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.knowly.dto.EducationDto;
import com.knowly.exceptions.InvalidEducationException;
import com.knowly.service.EducationService;
import com.knowly.service.UserService;

@Controller
public class EducationController {

    private final EducationService educationService;
    private final UserService userService;

    public EducationController(EducationService educationService, UserService userService) {
        this.educationService = educationService;
        this.userService = userService;
    }

    @GetMapping("/profile/edit/education")
    public String showEducationPage(Authentication auth, Model model) {
        model.addAttribute("profile", userService.getProfile(auth.getName()));
        model.addAttribute("educationList", educationService.findAll(auth.getName()));
        model.addAttribute("education", new EducationDto());
        return "Educationedit";
    }

    @PostMapping("/profile/edit/education")
    public String saveEducation(@ModelAttribute EducationDto dto, Authentication auth, Model model) {
        try {
            educationService.save(dto, auth.getName());
            return "redirect:/profile/edit/education";
        } catch (InvalidEducationException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("educationList", educationService.findAll(auth.getName()));
            model.addAttribute("education", dto);
            return "Educationedit";
        }
    }

    @PostMapping("/profile/edit/education/delete")
    public String deleteEducation(String educationId, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            educationService.deleteById(educationId, auth.getName());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Unable to delete. Please try again.");
        }
        return "redirect:/profile/edit/education";
    }
}
