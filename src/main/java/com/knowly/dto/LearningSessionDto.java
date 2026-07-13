package com.knowly.dto;

public class LearningSessionDto {
	private String sessionId;
	private String subject;
	private String description;
	private String helperName;
	private String helperInitials;
	private String helperProfilePicturepath;
	private String skillName;
	private String timeAgo;
	private double waitOpacity;
	private String tab;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHelperName() {
		return helperName;
	}

	public void setHelperName(String helperName) {
		this.helperName = helperName;
	}

	public String getHelperInitials() {
		return helperInitials;
	}

	public void setHelperInitials(String helperInitials) {
		this.helperInitials = helperInitials;
	}

	public String getHelperProfilePicturepath() {
		return helperProfilePicturepath;
	}

	public void setHelperProfilePicturepath(String helperProfilePicturepath) {
		this.helperProfilePicturepath = helperProfilePicturepath;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public String getTimeAgo() {
		return timeAgo;
	}

	public void setTimeAgo(String timeAgo) {
		this.timeAgo = timeAgo;
	}

	public double getWaitOpacity() {
		return waitOpacity;
	}

	public void setWaitOpacity(double waitOpacity) {
		this.waitOpacity = waitOpacity;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}
}
