package com.knowly.dto;

public class HelpingSessionDto {
	private String sessionId;
	private String subject;
	private String description;
	private String requesterName;
	private String requesterInitials;
	private String requesterProfilePicturepath;
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

	public String getRequesterName() {
		return requesterName;
	}

	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}

	public String getRequesterInitials() {
		return requesterInitials;
	}

	public void setRequesterInitials(String requesterInitials) {
		this.requesterInitials = requesterInitials;
	}

	public String getRequesterProfilePicturepath() {
		return requesterProfilePicturepath;
	}

	public void setRequesterProfilePicturepath(String requesterProfilePicturepath) {
		this.requesterProfilePicturepath = requesterProfilePicturepath;
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
