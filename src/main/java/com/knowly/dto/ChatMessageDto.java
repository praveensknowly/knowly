package com.knowly.dto;

public class ChatMessageDto {
	private String messageId;
	private String text;
	private String type;
	private String attachmentUrl;
	private String attachmentOriginalName;
	private String timeLabel;
	private String dateLabel;
	private String senderName;
	private boolean isMine;
	private boolean isFromHelper;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTimeLabel() {
		return timeLabel;
	}

	public void setTimeLabel(String timeLabel) {
		this.timeLabel = timeLabel;
	}

	public String getDateLabel() {
		return dateLabel;
	}

	public void setDateLabel(String dateLabel) {
		this.dateLabel = dateLabel;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public boolean isMine() {
		return isMine;
	}

	public void setMine(boolean mine) {
		isMine = mine;
	}

	public boolean isFromHelper() {
		return isFromHelper;
	}

	public void setFromHelper(boolean fromHelper) {
		isFromHelper = fromHelper;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	public String getAttachmentOriginalName() {
		return attachmentOriginalName;
	}

	public void setAttachmentOriginalName(String attachmentOriginalName) {
		this.attachmentOriginalName = attachmentOriginalName;
	}
}
