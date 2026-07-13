package com.knowly.dto;

import java.util.Set;

import com.knowly.entity.Skill;

public class SearchProfileDto {
	private String name;
	private String location;
	private String bio;
	private double overallRating;
	private String profilePicturepath;
	private int certificationCount;
	private String id;
	private int educationCount;
	private Set<Skill> skills;
	
	public Set<Skill> getSkills() {
		return skills;
	}
	public void setSkills(Set<Skill> skills) {
		this.skills = skills;
	}
	@Override
	public String toString() {
		return "SearchProfileDto [name=" + name + ", location=" + location + ", bio=" + bio + ", overallRating="
				+ overallRating + ", profilePicturepath=" + profilePicturepath + ", certificationCount="
				+ certificationCount + ", id=" + id + ", educationCount=" + educationCount + ", skills=" + skills + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public double getOverallRating() {
		return overallRating;
	}
	public void setOverallRating(double overallRating) {
		this.overallRating = overallRating;
	}
	public String getProfilePicturepath() {
		return profilePicturepath;
	}
	public void setProfilePicturepath(String profilePicturepath) {
		this.profilePicturepath = profilePicturepath;
	}
	public int getCertificationCount() {
		return certificationCount;
	}
	public void setCertificationCount(int certificationCount) {
		this.certificationCount = certificationCount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getEducationCount() {
		return educationCount;
	}
	public void setEducationCount(int educationCount) {
		this.educationCount = educationCount;
	}
	
	
}
