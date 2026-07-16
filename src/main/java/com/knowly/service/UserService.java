package com.knowly.service;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.knowly.dto.EditDto;
import com.knowly.dto.SignupDto;
import com.knowly.entity.Language;
import com.knowly.entity.User;
import com.knowly.entity.UserProfile;
import com.knowly.exceptions.UserAlreadyExistException;
import com.knowly.exceptions.UserNotFoundException;
import com.knowly.mapper.UserMapper;
import com.knowly.mapper.UserProfileMapper;
import com.knowly.repository.LanguageRepository;
import com.knowly.repository.ProfileRepository;
import com.knowly.repository.UserRepository;

@Service
public class UserService {
	private LanguageRepository languageRepo;
	private PasswordEncoder passwordEncoder;

	private UserRepository userRepo;

	private ProfileRepository profileRepo;
	
	private FileStorageService fileStorageService;
	
	private EmailVerificationService emailVerificationService;
	

	public UserService(LanguageRepository languageRepo, PasswordEncoder passwordEncoder, UserRepository userRepo,
		    ProfileRepository profileRepo, FileStorageService fileStorageService,
		    EmailVerificationService emailVerificationService) {
	super();
	this.languageRepo = languageRepo;
	this.passwordEncoder = passwordEncoder;
	this.userRepo = userRepo;
	this.profileRepo = profileRepo;
	this.fileStorageService = fileStorageService;
	this.emailVerificationService = emailVerificationService;
}


	@Transactional
	public void save(SignupDto signup) {
		User user = UserMapper.toUser(signup);

		if (userRepo.existsByEmail(user.getEmail())) {
			throw new UserAlreadyExistException("Try signing in or use a different email address.");
		}
		if (userRepo.existsByNumber(user.getNumber())) {
			throw new UserAlreadyExistException("Please sign in or use another number.");
		}

		if (!emailVerificationService.consumeVerifiedEmail(user.getEmail())) {
			throw new IllegalStateException("Please verify your email before signing up.");
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setCreatedAt(LocalDateTime.now());
		user.setEmailVerified(true);
		try {
			userRepo.save(user);
		} catch (DataIntegrityViolationException e) {
			throw new UserAlreadyExistException("Try signing in or use a different email address.");
		}

		UserProfile profile = new UserProfile();
		profile.setUser(user);
		profileRepo.save(profile);
	}
	@Transactional
	public void update(EditDto dto,String email){
		User user = this.findByEmail(email);
		UserProfile userProfile=user.getProfile();

		// Password confirmation gate for users who have a password set
		if (user.getPassword() != null) {
			if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isBlank()) {
				throw new IllegalArgumentException("Current password is required to make changes.");
			}
			if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
				throw new IllegalArgumentException("Incorrect current password. Please try again.");
			}
		}

		// Number update with uniqueness check
		if (dto.getNumber() != null && !dto.getNumber().isBlank()) {
			if (!dto.getNumber().equals(user.getNumber())) {
				if (userRepo.existsByNumberAndIdNot(dto.getNumber(), user.getId())) {
					throw new IllegalArgumentException("This phone number is already in use by another account.");
				}
				user.setNumber(dto.getNumber());
			}
		}

		// Password change logic
		if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
			if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
				throw new IllegalArgumentException("New password and confirm password do not match.");
			}
			if (dto.getNewPassword().length() < 6) {
				throw new IllegalArgumentException("Password must be at least 6 characters long.");
			}
			user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		}

		UserProfileMapper.updateFromDto(dto,userProfile);
		MultipartFile profilePicture = dto.getProfilePicture();
		if (profilePicture != null && !profilePicture.isEmpty()) {
		    String newFile = fileStorageService.store(profilePicture);
		    String oldFile = userProfile.getProfilePicturepath();
		    userProfile.setProfilePicturepath(newFile);

		    if (oldFile != null && !oldFile.equals(newFile)) {
		        fileStorageService.delete(oldFile);
		    }
		}
		Set<Language> languages = Collections.emptySet();
		if (dto.getLanguages() != null && !dto.getLanguages().isEmpty()) {
			Set<String> uniqueLanguageNames = new java.util.HashSet<>(dto.getLanguages());
			languages = languageRepo.findByNameIn(new java.util.ArrayList<>(uniqueLanguageNames));
			if (languages.size() != uniqueLanguageNames.size()) {
			    throw new IllegalArgumentException("Invalid language selected.");
			}
		}

		userProfile.setLanguages(languages);

		profileRepo.save(userProfile);
		userRepo.save(user);
	}


	public UserProfile getProfile(String email) {
		User user = findByEmail(email);
		return user.getProfile();
	}


	public User findByEmail(String email) {
		// TODO Auto-generated method stub
		return  userRepo.findByEmail(email)
		        .orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	public List<UserProfile> getSuggestedExperts(String email) {
		User currentUser = findByEmail(email);
		Pageable pageable = PageRequest.of(0, 5);
		return profileRepo.findRecentProfiles(currentUser.getId(), pageable);
	}

	@Transactional
	public void setInitialPassword(String email, String newPassword, String confirmPassword) {
		User user = findByEmail(email);

		if (user.getPassword() != null) {
			return; // already set elsewhere — nothing to do, ignore silently
		}
		if (newPassword == null || newPassword.isBlank()) {
			return; // treated as "skip"
		}
		if (!newPassword.equals(confirmPassword)) {
			throw new IllegalArgumentException("Password and confirm password do not match.");
		}
		if (newPassword.length() < 6) {
			throw new IllegalArgumentException("Password must be at least 6 characters long.");
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepo.save(user);
	}

	@Transactional
	public boolean resetPassword(String email, String newPassword) {
		var userOpt = userRepo.findByEmail(email);
		if (userOpt.isEmpty() || !"LOCAL".equals(userOpt.get().getProvider())) {
			return false; // no account, or it's a Google/GitHub account with no password to reset
		}
		User user = userOpt.get();
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepo.save(user);
		return true;
	}
}
