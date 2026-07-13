package com.knowly.mapper;

import com.knowly.dto.EditDto;
import com.knowly.dto.ExpertDto;
import com.knowly.entity.Language;
import com.knowly.entity.UserProfile;

public class UserProfileMapper {
	
	public static void updateFromDto(EditDto dto,UserProfile profile) {
		profile.setBio(dto.getBio());
		profile.setDob(dto.getDob());
		profile.setGender(dto.getGender());
		profile.setLocation(dto.getLocation());
	}
	public static EditDto profileToDto(UserProfile profile) {
	    EditDto dto = new EditDto();

	    dto.setBio(profile.getBio());
	    dto.setDob(profile.getDob());
	    dto.setGender(profile.getGender());
	    dto.setLocation(profile.getLocation());
	    dto.setLanguages(
	            profile.getLanguages()
	                   .stream()
	                   .map(Language::getName)
	                   .toList()
	    );
	    return dto;
	}
	public static ExpertDto toExpertDto(UserProfile profile) {
		ExpertDto dto=new ExpertDto();
		dto.setName(profile.getUser().getName());
		dto.setBio(profile.getBio());
		dto.setCertifications(profile.getCertifications());
		dto.setEducation(profile.getEducation());
		dto.setId(profile.getId());
		dto.setLanguages(profile.getLanguages());
		dto.setLocation(profile.getLocation());
		dto.setProfilePicturepath(profile.getProfilePicturepath());
		dto.setSkills(profile.getSkills());
		dto.setProjects(profile.getProjects());
		return dto;
		
	}
}
