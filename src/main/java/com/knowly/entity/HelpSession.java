package com.knowly.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.knowly.converter.HelpSessionStatusConverter;
import com.knowly.enums.HelpSessionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

@Entity
public class HelpSession {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String sessionId;
	@Version
	private Long version;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="requester")
	private UserProfile requester;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="helper")
	private UserProfile helper;
	@OneToMany(mappedBy = "session",cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Message> messages=new ArrayList<Message>();
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "skill_id")
	private Skill skill;
	
	public Skill getSkill() {
		return skill;
	}
	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	@Column(length=150)
	private String subject;
	
	@Convert(converter = HelpSessionStatusConverter.class)
	private HelpSessionStatus status;
	@CreationTimestamp
	private LocalDateTime createdAt;
	private LocalDateTime startedAt;
	private LocalDateTime endedAt;
	private LocalDateTime expiresAt;
	private LocalDateTime ratedAt;
	private LocalDateTime firstExpertReplyAt;
	private LocalDateTime sessionExpiresAt;
	private String expiredReason;
	public LocalDateTime getRatedAt() {
		return ratedAt;
	}
	public void setRatedAt(LocalDateTime ratedAt) {
		this.ratedAt = ratedAt;
	}
	@Override
	public String toString() {
		return "HelpSession [sessionId=" + sessionId + ", subject=" + subject + ", status=" + status + ", createdAt=" + createdAt + ", startedAt=" + startedAt + ", endedAt="
				+ endedAt + ", expiresAt=" + expiresAt + ", ratedAt=" + ratedAt + "]";
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public UserProfile getRequester() {
		return requester;
	}
	public void setRequester(UserProfile requester) {
		this.requester = requester;
	}
	public UserProfile getHelper() {
		return helper;
	}
	public void setHelper(UserProfile helper) {
		this.helper = helper;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public HelpSessionStatus getStatus() {
		return status;
	}
	public void setStatus(HelpSessionStatus status) {
		this.status = status;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}
	public LocalDateTime getEndedAt() {
		return endedAt;
	}
	public void setEndedAt(LocalDateTime endedAt) {
		this.endedAt = endedAt;
	}
	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
	public LocalDateTime getFirstExpertReplyAt() {
		return firstExpertReplyAt;
	}
	public void setFirstExpertReplyAt(LocalDateTime firstExpertReplyAt) {
		this.firstExpertReplyAt = firstExpertReplyAt;
	}
	public LocalDateTime getSessionExpiresAt() {
		return sessionExpiresAt;
	}
	public void setSessionExpiresAt(LocalDateTime sessionExpiresAt) {
		this.sessionExpiresAt = sessionExpiresAt;
	}
	public String getExpiredReason() {
		return expiredReason;
	}
	public void setExpiredReason(String expiredReason) {
		this.expiredReason = expiredReason;
	}
}
