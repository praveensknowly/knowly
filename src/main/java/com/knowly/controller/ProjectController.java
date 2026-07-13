package com.knowly.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.knowly.dto.ProjectDto;
import com.knowly.exceptions.InvalidProjectException;
import com.knowly.service.ProjectService;
import com.knowly.service.UserService;

@Controller
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping("/profile/edit/projects")
    public String showProjectPage(Authentication auth, Model model) {
        model.addAttribute("profile", userService.getProfile(auth.getName()));
        model.addAttribute("projects", projectService.findAll(auth.getName()));
        model.addAttribute("project", new ProjectDto());
        return "Projectedit";
    }

    @PostMapping("/profile/edit/projects")
    public String saveProject(@ModelAttribute ProjectDto dto, Authentication auth, Model model) {
        try {
            projectService.save(dto, auth.getName());
            return "redirect:/profile/edit/projects";
        } catch (InvalidProjectException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("projects", projectService.findAll(auth.getName()));
            model.addAttribute("project", dto);
            return "Projectedit";
        }
    }

    @PostMapping("/profile/edit/projects/delete")
    public String deleteProject(String projectId, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            projectService.deleteById(projectId, auth.getName());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Unable to delete. Please try again.");
        }
        return "redirect:/profile/edit/projects";
    }
}
