package com.knowly.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.knowly.dto.SkillDto;
import com.knowly.entity.User;
import com.knowly.exceptions.SkillAlreadyExistsException;
import com.knowly.service.SkillService;
import com.knowly.service.UserService;


@Controller
public class SkillController {
	private final SkillService skillService;
	private final UserService userService;
	
	
	public SkillController(SkillService skillService,UserService userService) {
		this.skillService = skillService;
		this.userService = userService;
	}

	@GetMapping("/profile/edit/skills")
	public String showSkillPage(Authentication auth, Model model) {
	    model.addAttribute("profile", userService.getProfile(auth.getName()));
	    model.addAttribute(
	        "skills",
	        skillService.findAll(auth.getName())
	    );

	    model.addAttribute(
	        "skill",
	        new SkillDto()
	    );

	    return "Skilledit";
	}
	
	@PostMapping("/profile/edit/skills")
	public String saveSkill(@ModelAttribute SkillDto dto, Authentication auth, Model model) {
	    try {
	        skillService.save(dto, auth.getName());
	        return "redirect:/profile/edit/skills";
	    } catch (SkillAlreadyExistsException e) {
	        model.addAttribute("error", e.getMessage());
	        model.addAttribute("skills", skillService.findAll(auth.getName()));
	        model.addAttribute("skill", dto);
	        return "Skilledit";
	    }
	}

	@PostMapping("/profile/edit/skills/delete")
	public String deleteSkill(String skillId, Authentication auth, RedirectAttributes redirectAttrs) {
		try {
			skillService.deleteById(skillId, auth.getName());
		} catch (Exception e) {
			redirectAttrs.addFlashAttribute("error", "Unable to delete. Please try again.");
		}
		return "redirect:/profile/edit/skills";
	}

	@GetMapping("/search")
	public String search(@RequestParam(required = false) String query, Authentication auth,Model model) {
		User user = userService.findByEmail(auth.getName());
		model.addAttribute("user", user);
		if (query != null && !query.isBlank()) {
			
			model.addAttribute("dtos", skillService.findAllProfiles(query));
		}
		model.addAttribute("query", query);
		return "Search";
	}

	@GetMapping("/search/{skill}")
	@ResponseBody
	public List<?> skillSearch(@PathVariable("skill") String name) {
		return skillService.findAllProfiles(name);
	}
}
