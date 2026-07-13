package com.knowly.controller;

import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.knowly.dto.EditDto;
import com.knowly.entity.UserProfile;
import com.knowly.exceptions.FileStorageException;
import com.knowly.exceptions.InvalidImageException;
import com.knowly.service.CertificationService;
import com.knowly.service.EducationService;
import com.knowly.service.UserService;

@Controller
public class ProfileContoller {
	
	private UserService userService;
	private EducationService educationService;
	private CertificationService certificationService;
	

	@GetMapping("/profile/edit")
	public String editProfile(Authentication auth, Model model) {
	    populateEditProfileModel(auth, model);
	    return "EditProfile";
	}
	
	@PostMapping("/profile/edit")
	public String editProfile(@ModelAttribute EditDto dto,
							@RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture,
							Authentication auth, Model model) {
	    try {
	        dto.setProfilePicture(profilePicture);
	        userService.update(dto, auth.getName());
	        return "redirect:/profile";
	    } catch (InvalidImageException | FileStorageException | IllegalArgumentException e) {
	        model.addAttribute("error", e.getMessage());
	        populateEditProfileModel(auth, model);
	        return "EditProfile";
	    }
	}

	
	public ProfileContoller(UserService userService, EducationService educationService,
			CertificationService certificationService) {
		super();
		this.userService = userService;
		this.educationService = educationService;
		this.certificationService = certificationService;
	}

	private void populateEditProfileModel(Authentication auth, Model model) {
	    UserProfile profile = userService.getProfile(auth.getName());
	    Set<?> educationList = educationService.findAll(auth.getName());
	    Set<?> certificationsList = certificationService.findAll(auth.getName());
	
	    model.addAttribute("profile", profile);
	    // Provide an EditDto pre-populated from the profile to support form binding
	    model.addAttribute("editDto", com.knowly.mapper.UserProfileMapper.profileToDto(profile));
	    model.addAttribute("educationCount", educationList == null ? 0 : educationList.size());
	    model.addAttribute("certificationsCount", certificationsList == null ? 0 : certificationsList.size());
	    model.addAttribute("profileCompletion", calculateProfileCompletion(profile, educationList, certificationsList));
	}
	@GetMapping("/profile")
	public String profile(Authentication auth, Model model) {
		UserProfile profile = userService.getProfile(auth.getName());
		Set<?> educationList = educationService.findAll(auth.getName());
		Set<?> certificationsList = certificationService.findAll(auth.getName());

		model.addAttribute("profile", profile);
		model.addAttribute("user", profile.getUser());
		model.addAttribute("educationList", educationList);
		model.addAttribute("certificationsList", certificationsList);

		int profileCompletion = calculateProfileCompletion(profile, educationList, certificationsList);
		model.addAttribute("profileCompletion", profileCompletion);

		return "Profile";
	}
	private int calculateProfileCompletion(UserProfile profile, Set<?> educationList, Set<?> certificationsList) {
	    int completion = 20;
	
	    if (profile != null && profile.getBio() != null && !profile.getBio().isBlank()) {
	        completion += 20;
	    }
	    if (profile != null && profile.getSkills() != null && !profile.getSkills().isEmpty()) {
	        completion += 20;
	    }
	    if (educationList != null && !educationList.isEmpty()) {
	        completion += 20;
	    }
	    if (certificationsList != null && !certificationsList.isEmpty()) {
	        completion += 20;
	    }
	    return Math.min(completion, 100);
	}
	
	
}
