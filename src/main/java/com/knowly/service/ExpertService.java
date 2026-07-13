package com.knowly.service;

import org.springframework.stereotype.Service;

import com.knowly.entity.UserProfile;
import com.knowly.exceptions.UserNotFoundException;
import com.knowly.repository.ProfileRepository;

@Service
public class ExpertService {
	private ProfileRepository profileRepo;
	
	public ExpertService(ProfileRepository profileRepo) {
		super();
		this.profileRepo = profileRepo;
	}



	public UserProfile findById(String id) {
		return profileRepo.findById(id).orElseThrow(()->new UserNotFoundException("User not Found"));
	}

//	public ExpertService(UserProfile profileRepo) {
//		super();
//		this.profileRepo = profileRepo;
//	}
	
	
}
