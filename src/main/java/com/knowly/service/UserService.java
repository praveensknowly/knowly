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
}
