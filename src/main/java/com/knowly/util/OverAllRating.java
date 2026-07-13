package com.knowly.util;

import java.util.Set;

import com.knowly.entity.Skill;
import com.knowly.entity.UserProfile;

public class OverAllRating {
	public static double calculate(UserProfile profile) {
	    Set<Skill> skills = profile.getSkills();
	    if (skills == null || skills.isEmpty()) {
	        return 0.0;
	    }
	    double total = 0.0;
	    for (Skill skill : skills) {
	        total += skill.getSkillScore();
	    }
	    return total / skills.size();
	}
}
