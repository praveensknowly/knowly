package com.knowly.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ChatSessionDto {
	private String sessionId;
	private String subject;
	private String skillName;
	private String status;
	private String requesterName;
	private String requesterProfilePicturepath;
	private String helperName;
	private String helperProfilePicturepath;
	private String myRole;
	private List<ChatMessageDto> messages;
	private LocalDateTime sessionExpiresAt;
	private Long sessionExpiresAtMs;
	private String expiredReason;
	private boolean showRatingPopup;

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

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRequesterName() {
		return requesterName;
	}

	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}

	public String getRequesterProfilePicturepath() {
		return requesterProfilePicturepath;
	}

	public void setRequesterProfilePicturepath(String requesterProfilePicturepath) {
		this.requesterProfilePicturepath = requesterProfilePicturepath;
	}

	public String getHelperName() {
		return helperName;
	}

	public void setHelperName(String helperName) {
		this.helperName = helperName;
	}

	public String getHelperProfilePicturepath() {
		return helperProfilePicturepath;
	}

	public void setHelperProfilePicturepath(String helperProfilePicturepath) {
		this.helperProfilePicturepath = helperProfilePicturepath;
	}

	public String getMyRole() {
		return myRole;
	}

	public void setMyRole(String myRole) {
		this.myRole = myRole;
	}

	public List<ChatMessageDto> getMessages() {
		return messages;
	}

	public void setMessages(List<ChatMessageDto> messages) {
		this.messages = messages;
	}

	public LocalDateTime getSessionExpiresAt() {
		return sessionExpiresAt;
	}

	public void setSessionExpiresAt(LocalDateTime sessionExpiresAt) {
		this.sessionExpiresAt = sessionExpiresAt;
	}

	public Long getSessionExpiresAtMs() {
		return sessionExpiresAtMs;
	}

	public void setSessionExpiresAtMs(Long sessionExpiresAtMs) {
		this.sessionExpiresAtMs = sessionExpiresAtMs;
	}

	public String getExpiredReason() {
		return expiredReason;
	}

	public void setExpiredReason(String expiredReason) {
		this.expiredReason = expiredReason;
	}

	public boolean isShowRatingPopup() {
		return showRatingPopup;
	}

	public void setShowRatingPopup(boolean showRatingPopup) {
		this.showRatingPopup = showRatingPopup;
	}
}
