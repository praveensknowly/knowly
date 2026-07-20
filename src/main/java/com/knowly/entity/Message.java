package com.knowly.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.knowly.enums.MessageType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;


@Entity
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String messageId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="session_id")
	private HelpSession session;
	@Enumerated(EnumType.STRING)
	private MessageType type;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="sender_id")
	private UserProfile sender;
	
	@Column(columnDefinition = "TEXT")
	private String message;

	@Column(name = "attachment_path")
	private String attachmentPath;

	@Column(name = "attachment_original_name")
	private String attachmentOriginalName;

	@Column(name = "attachment_mime_type")
	private String attachmentMimeType;

	private Long attachmentSize;

	@CreationTimestamp
	private LocalDateTime sentAt;
	private boolean isRead;
	@Override
	public String toString() {
		return "Message [messageId=" + messageId + ", type=" + type + ", message=" + message + ", sentAt=" + sentAt
				+ ", isRead=" + isRead + "]";
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public HelpSession getSession() {
		return session;
	}
	public void setSession(HelpSession session) {
		this.session = session;
	}
	public MessageType getType() {
		return type;
	}
	public void setType(MessageType type) {
		this.type = type;
	}
	public UserProfile getSender() {
		return sender;
	}
	public void setSender(UserProfile sender) {
		this.sender = sender;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public LocalDateTime getSentAt() {
		return sentAt;
	}
	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getAttachmentPath() {
		return attachmentPath;
	}

	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}

	public String getAttachmentOriginalName() {
		return attachmentOriginalName;
	}

	public void setAttachmentOriginalName(String attachmentOriginalName) {
		this.attachmentOriginalName = attachmentOriginalName;
	}

	public String getAttachmentMimeType() {
		return attachmentMimeType;
	}

	public void setAttachmentMimeType(String attachmentMimeType) {
		this.attachmentMimeType = attachmentMimeType;
	}

	public Long getAttachmentSize() {
		return attachmentSize;
	}

	public void setAttachmentSize(Long attachmentSize) {
		this.attachmentSize = attachmentSize;
	}


}
