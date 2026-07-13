package com.knowly.dto;

public class HelpRequestDto {
	private String helperId;
	private String subject;
	private String skillId;
	private String description;
	public String getHelperId() {
		return helperId;
	}
	public void setHelperId(String helperId) {
		this.helperId = helperId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSkillId() {
		return skillId;
	}
	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "HelpRequestDto [helperId=" + helperId + ", subject=" + subject + ", skillId=" + skillId
				+ ", description=" + description + "]";
	}
	
}
