package com.knowly.controller;



import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.knowly.dto.SignupDto;
import com.knowly.entity.UserProfile;
import com.knowly.exceptions.UserAlreadyExistException;
import com.knowly.service.CertificationService;
import com.knowly.service.EducationService;
import com.knowly.service.UserService;
import com.knowly.util.ProfileCompletion;


@Controller
public class HomeController {
	private final UserService userService;
	private EducationService educationService;
	private CertificationService certificationService;

	
	public HomeController(UserService userService, EducationService educationService,
			CertificationService certificationService) {
		super();
		this.userService = userService;
		this.educationService = educationService;
		this.certificationService = certificationService;
	}

	@GetMapping("/")
	public String hello(Authentication auth) {
		if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null
				&& !"anonymousUser".equals(auth.getName())) {
			return "redirect:/home";
		}
		return "Launch";
	}
	
	@GetMapping("/signup")
	public String signup() {
		return "SignUp";
	}
	
	@PostMapping("/signup")
	public String signup(@ModelAttribute SignupDto dto, Model model) {
	    try {
	        userService.save(dto);
	        return "redirect:/login";

	    } catch (UserAlreadyExistException e) {
	        model.addAttribute("error", e.getMessage());
	        model.addAttribute("signup", dto);
	        return "SignUp";

	    } catch (IllegalStateException e) {
	        model.addAttribute("error", e.getMessage());
	        model.addAttribute("signup", dto);
	        return "SignUp";
	    }
	}
	
	@GetMapping("/login")
	public String login() {
		return "Login";
	}

	@GetMapping("/favicon.ico")
	public ResponseEntity<Void> favicon() {
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/home")
	public String home(Authentication auth, Model model) {
		var profile = userService.getProfile(auth.getName());
		Set<?> educationList = educationService.findAll(auth.getName());
		Set<?> certificationsList = certificationService.findAll(auth.getName());
		List<UserProfile> suggestedExperts = userService.getSuggestedExperts(auth.getName());

		model.addAttribute("profile", profile);
		model.addAttribute("user", profile.getUser());
		model.addAttribute("educationList", educationList);
		model.addAttribute("certificationsList", certificationsList);
		model.addAttribute("profileCompletion", Math.min(ProfileCompletion.calculate(profile, educationList, certificationsList), 100));
		model.addAttribute("suggestedExperts", suggestedExperts);

		return "Home";
	}
	
	
}
