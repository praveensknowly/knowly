package com.knowly.dto;

import java.util.Set;

import com.knowly.entity.Certification;
import com.knowly.entity.Education;
import com.knowly.entity.Language;
import com.knowly.entity.Project;
import com.knowly.entity.Skill;

public class ExpertDto {
	private String name;
	private String bio;
	private String id;
	private Set<Skill> skills;
	private Set<Project> projects;
	private Set<Certification> certifications;
	private Set<Education> education;
	private String profilePicturepath;
	private String location;
	private Set<Language> languages;
	@Override
	public String toString() {
		return "ExpertDto [name=" + name + ", bio=" + bio + ", id=" + id + ", skills=" + skills + ", projects="
				+ projects + ", certifications=" + certifications + ", education=" + education + ", profilePicturepath="
				+ profilePicturepath + ", location=" + location + ", languages=" + languages + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Set<Skill> getSkills() {
		return skills;
	}
	public void setSkills(Set<Skill> skills) {
		this.skills = skills;
	}
	public Set<Project> getProjects() {
		return projects;
	}
	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	public Set<Certification> getCertifications() {
		return certifications;
	}
	public void setCertifications(Set<Certification> certifications) {
		this.certifications = certifications;
	}
	public Set<Education> getEducation() {
		return education;
	}
	public void setEducation(Set<Education> education) {
		this.education = education;
	}
	public String getProfilePicturepath() {
		return profilePicturepath;
	}
	public void setProfilePicturepath(String profilePicturepath) {
		this.profilePicturepath = profilePicturepath;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Set<Language> getLanguages() {
		return languages;
	}
	public void setLanguages(Set<Language> languages) {
		this.languages = languages;
	}
	
}
