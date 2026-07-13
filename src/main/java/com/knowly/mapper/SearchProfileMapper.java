package com.knowly.mapper;

import com.knowly.dto.SearchProfileDto;
import com.knowly.entity.UserProfile;

public class SearchProfileMapper {
	public static SearchProfileDto toSearchProfileDto(UserProfile profile) {
		SearchProfileDto dto=new SearchProfileDto();
		dto.setBio(profile.getBio());
		dto.setName(profile.getUser().getName());
		dto.setCertificationCount(profile.getCertifications().size());
		dto.setId(profile.getId());
		dto.setLocation(profile.getLocation());
		dto.setOverallRating(profile.getOverallRating());
		dto.setProfilePicturepath(profile.getProfilePicturepath());
		dto.setEducationCount(profile.getEducation().size());
		dto.setSkills(profile.getSkills());
		return dto;
	}
}
