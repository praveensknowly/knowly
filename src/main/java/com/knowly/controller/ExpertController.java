package com.knowly.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.knowly.entity.UserProfile;
import com.knowly.mapper.UserProfileMapper;
import com.knowly.service.ExpertService;
import com.knowly.service.UserService;

@Controller
public class ExpertController {
	private ExpertService expertService;
	private final UserService userService;

	public ExpertController(ExpertService expertService, UserService userService) {
		super();
		this.expertService = expertService;
		this.userService = userService;
	}
	
	@GetMapping("/experts/{id}")
	public String expertProfile(@PathVariable String id,Model model,Authentication auth) {
		UserProfile profile=expertService.findById(id);
		model.addAttribute("user",userService.findByEmail(auth.getName()));
		model.addAttribute("profile",UserProfileMapper.toExpertDto(profile));
		return "Expert";
	}
}
