package com.knowly.controller;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import com.knowly.dto.EditDto;
import com.knowly.entity.User;
import com.knowly.entity.UserProfile;
import com.knowly.exceptions.FileStorageException;
import com.knowly.exceptions.InvalidImageException;
import com.knowly.repository.UserRepository;
import com.knowly.service.CertificationService;
import com.knowly.service.EducationService;
import com.knowly.service.EmailVerificationService;
import com.knowly.service.UserService;

@Controller
public class ProfileContoller {
	
	private UserService userService;
	private EducationService educationService;
	private CertificationService certificationService;
	private EmailVerificationService emailVerificationService;
	private UserRepository userRepository;
	private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

	private Bucket resolveBucket(String key) {
		return buckets.computeIfAbsent(key, k -> Bucket.builder()
			.addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(15))))
			.build());
	}

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

	@PostMapping("/profile/edit/email/send-code")
	@ResponseBody
	public String sendEmailChangeCode(@RequestBody String newEmail, Authentication auth) {
		User user = userService.findByEmail(auth.getName());
		
		// Rate limit by user's current email
		if (!resolveBucket(user.getEmail()).tryConsume(1)) {
			return "error: Too many requests. Please try again later.";
		}
		
		// Check if email is already taken by another user
		if (userRepository.existsByEmail(newEmail) && !newEmail.equals(user.getEmail())) {
			return "error: This email is already in use by another account.";
		}
		
		// Send OTP to new email
		emailVerificationService.sendOtp(newEmail);
		return "success";
	}

	@PostMapping("/profile/edit/email")
	@ResponseBody
	public String confirmEmailChange(@RequestBody EmailChangeRequest request, Authentication auth) {
		User user = userService.findByEmail(auth.getName());
		
		// Verify OTP
		if (!emailVerificationService.verifyOtp(request.getNewEmail(), request.getCode())) {
			return "error: Invalid or expired verification code.";
		}
		
		// Update email
		user.setEmail(request.getNewEmail());
		user.setEmailVerified(true);
		
		try {
			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			return "error: That email was just taken by another account.";
		}
		
		return "success";
	}

	public static class EmailChangeRequest {
		private String newEmail;
		private String code;

		public String getNewEmail() {
			return newEmail;
		}

		public void setNewEmail(String newEmail) {
			this.newEmail = newEmail;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}

	
	public ProfileContoller(UserService userService, EducationService educationService,
			CertificationService certificationService, EmailVerificationService emailVerificationService,
			UserRepository userRepository) {
		super();
		this.userService = userService;
		this.educationService = educationService;
		this.certificationService = certificationService;
		this.emailVerificationService = emailVerificationService;
		this.userRepository = userRepository;
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
