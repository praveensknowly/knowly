package com.knowly.util;

import java.util.Set;


import com.knowly.entity.UserProfile;

public class ProfileCompletion {
	public static int calculate(UserProfile profile,Set<?> educationList,Set<?> certificationsList) {
		
		int profileCompletion = 20;
		if (profile != null && profile.getBio() != null && !profile.getBio().isBlank()) {
			profileCompletion += 20;
		}
		if (profile != null && profile.getSkills() != null && !profile.getSkills().isEmpty()) {
			profileCompletion += 20;
		}
		if (educationList != null && !educationList.isEmpty()) {
			profileCompletion += 20;
		}
		if (certificationsList != null && !certificationsList.isEmpty()) {
			profileCompletion += 20;
		}
		return profileCompletion;
	}
}
