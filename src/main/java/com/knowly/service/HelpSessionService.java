package com.knowly.service;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.knowly.dto.ChatMessageDto;
import com.knowly.dto.ChatSessionDto;
import com.knowly.dto.HelpRequestDto;
import com.knowly.dto.HelpingSessionDto;
import com.knowly.dto.LearningSessionDto;
import com.knowly.entity.HelpSession;
import com.knowly.entity.Message;
import com.knowly.entity.UserProfile;
import com.knowly.enums.HelpSessionStatus;
import com.knowly.enums.MessageType;
import com.knowly.exceptions.UserNotFoundException;
import com.knowly.repository.HelpSessionRepository;
import com.knowly.repository.MessageRepository;

import com.knowly.repository.ProfileRepository;
import com.knowly.repository.RatingRepository;
import com.knowly.repository.SkillRepository;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.knowly.service.PushNotificationService;



@Service
public class HelpSessionService {
	private ProfileRepository profileRepo;
	private HelpSessionRepository sessionRepo;
	private SkillRepository skillRepo;
	private UserService userService;
	private RatingRepository ratingRepo;
	private MessageRepository messageRepo;
	private PushNotificationService pushNotificationService;

	@Autowired
	@Lazy
	private HelpSessionService self;


	public HelpSessionService(ProfileRepository profileRepo, HelpSessionRepository sessionRepo,
			SkillRepository skillRepo, UserService userService, RatingRepository ratingRepo,
			MessageRepository messageRepo, PushNotificationService pushNotificationService) {
		super();
		this.profileRepo = profileRepo;
		this.sessionRepo = sessionRepo;
		this.skillRepo = skillRepo;
		this.userService = userService;
		this.ratingRepo = ratingRepo;
		this.messageRepo = messageRepo;
		this.pushNotificationService = pushNotificationService;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void expireSession(HelpSession session, HelpSessionStatus newStatus, String reason) {
		session.setStatus(newStatus);
		session.setExpiredReason(reason);
		if (newStatus == HelpSessionStatus.IGNORED) {
			session.setEndedAt(LocalDateTime.now());
		}
		sessionRepo.save(session);
	}

	@Transactional
	public void save(HelpRequestDto dto,String email) {
		UserProfile sender=userService.getProfile(email);
		if (dto.getHelperId() == null || dto.getHelperId().isBlank()) {
		    throw new IllegalArgumentException("Please select a helper.");
		}
		if (sender.getId().equals(dto.getHelperId())) {
		    throw new IllegalArgumentException("You cannot send a help request to yourself.");
		}
		HelpSession session=new HelpSession();
		session.setHelper(profileRepo.findById(dto.getHelperId()).orElseThrow(()->new UserNotFoundException("User Not FOund")));
		session.setRequester(sender);
		session.setStatus(HelpSessionStatus.PENDING);
		session.setSubject(dto.getSubject());
		session.setExpiresAt(LocalDateTime.now().plusHours(22));
		Message message=new Message();
		message.setMessage(dto.getDescription());
		message.setRead(false);
		message.setSender(sender);
		message.setSession(session);
		message.setType(MessageType.Text);
		session.setSkill(skillRepo.findById(dto.getSkillId()).orElseThrow(()-> new RuntimeException("Skill not found")));
		session.getMessages().add(message);
		sessionRepo.save(session);

		// Capture variables for push notification after transaction commits
		UserProfile helper = session.getHelper();
		String senderName = sender.getUser() != null ? sender.getUser().getName() : "Someone";
		String subject = session.getSubject();
		String sessionId = session.getSessionId();

		// Register push notification to fire after transaction commits
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				pushNotificationService.notifyUser(
					helper,
					"New Help Request",
					senderName + " needs help with: " + subject,
					"/helping"
				);
			}
		});

	}

	public List<HelpingSessionDto> findForHelper(String email) {
		UserProfile helper = userService.getProfile(email);
		List<HelpSession> sessions = sessionRepo.findByHelperIdWithDetails(helper.getId());
		if (sessions.isEmpty()) {
			return List.of();
		}

		// Filter out ignored sessions
		sessions = sessions.stream()
				.filter(session -> session.getStatus() != HelpSessionStatus.IGNORED)
				.toList();

		if (sessions.isEmpty()) {
			return List.of();
		}

		LocalDateTime oldest = sessions.stream()
				.map(HelpSession::getCreatedAt)
				.filter(java.util.Objects::nonNull)
				.min(Comparator.naturalOrder())
				.orElse(LocalDateTime.now());

		List<HelpingSessionDto> dtos = new ArrayList<>();
		for (HelpSession session : sessions) {
			HelpingSessionDto dto = new HelpingSessionDto();
			dto.setSessionId(session.getSessionId());
			dto.setSubject(session.getSubject());
			dto.setDescription(firstMessageText(session));
			dto.setRequesterName(requesterName(session));
			dto.setRequesterInitials(initials(dto.getRequesterName()));
			dto.setRequesterProfilePicturepath(requesterProfilePicture(session));
			dto.setSkillName(session.getSkill() != null ? session.getSkill().getName() : "General");
			dto.setTimeAgo(formatRelativeTime(session.getCreatedAt()));
			dto.setWaitOpacity(waitOpacity(session.getCreatedAt(), oldest));
			dto.setTab(tabForStatus(session.getStatus()));
			dtos.add(dto);
		}
		return dtos;
	}

	public List<LearningSessionDto> findForRequester(String email) {
		UserProfile requester = userService.getProfile(email);
		List<HelpSession> sessions = sessionRepo.findByRequesterIdWithDetails(requester.getId());
		if (sessions.isEmpty()) {
			return List.of();
		}

		// Filter out ignored sessions
		sessions = sessions.stream()
				.filter(session -> session.getStatus() != HelpSessionStatus.IGNORED)
				.toList();

		if (sessions.isEmpty()) {
			return List.of();
		}

		LocalDateTime oldest = sessions.stream()
				.map(HelpSession::getCreatedAt)
				.filter(java.util.Objects::nonNull)
				.min(Comparator.naturalOrder())
				.orElse(LocalDateTime.now());

		List<LearningSessionDto> dtos = new ArrayList<>();
		for (HelpSession session : sessions) {
			LearningSessionDto dto = new LearningSessionDto();
			dto.setSessionId(session.getSessionId());
			dto.setSubject(session.getSubject());
			dto.setDescription(firstMessageText(session));
			dto.setHelperName(helperName(session));
			dto.setHelperInitials(initials(dto.getHelperName()));
			dto.setHelperProfilePicturepath(helperProfilePicture(session));
			dto.setSkillName(session.getSkill() != null ? session.getSkill().getName() : "General");
			dto.setTimeAgo(formatRelativeTime(session.getCreatedAt()));
			dto.setWaitOpacity(waitOpacity(session.getCreatedAt(), oldest));
			dto.setTab(tabForStatus(session.getStatus()));
			dtos.add(dto);
		}
		return dtos;
	}

	private String firstMessageText(HelpSession session) {
		if (session.getMessages() == null || session.getMessages().isEmpty()) {
			return "No description provided.";
		}
		return session.getMessages().stream()
				.sorted(Comparator.comparing(Message::getSentAt, Comparator.nullsLast(Comparator.naturalOrder())))
				.map(Message::getMessage)
				.filter(msg -> msg != null && !msg.isBlank())
				.findFirst()
				.orElse("No description provided.");
	}

	private String requesterName(HelpSession session) {
		if (session.getRequester() == null || session.getRequester().getUser() == null) {
			return "Unknown user";
		}
		return session.getRequester().getUser().getName();
	}

	private String helperName(HelpSession session) {
		if (session.getHelper() == null || session.getHelper().getUser() == null) {
			return "Unknown user";
		}
		return session.getHelper().getUser().getName();
	}

	private String requesterProfilePicture(HelpSession session) {
		if (session.getRequester() == null) {
			return null;
		}
		return session.getRequester().getProfilePicturepath();
	}

	private String helperProfilePicture(HelpSession session) {
		if (session.getHelper() == null) {
			return null;
		}
		return session.getHelper().getProfilePicturepath();
	}

	private String initials(String name) {
		if (name == null) {
			return "??";
		}
		String trimmed = name.trim();
		if (trimmed.isEmpty()) {
			return "??";
		}
		if (trimmed.equals("Unknown user")) {
			return "UU";
		}
		String[] parts = trimmed.split("\\s+");
		if (parts.length == 0) {
			return "??";
		}
		if (parts.length == 1) {
			String first = parts[0];
			if (first.isEmpty()) {
				return "??";
			}
			return first.substring(0, Math.min(1, first.length())).toUpperCase();
		}
		String first = parts[0];
		String second = parts[1];
		if (first.isEmpty() && second.isEmpty()) {
			return "??";
		}
		if (first.isEmpty()) {
			return second.substring(0, Math.min(1, second.length())).toUpperCase();
		}
		if (second.isEmpty()) {
			return first.substring(0, Math.min(1, first.length())).toUpperCase();
		}
		return (first.substring(0, Math.min(1, first.length())) + second.substring(0, Math.min(1, second.length()))).toUpperCase();
	}

	private String tabForStatus(HelpSessionStatus status) {
		if (status == null) {
			return "pending";
		}
		return switch (status) {
			case ACTIVE -> "active";
			case COMPLETED, EXPIRED -> "solved";
			default -> "pending";
		};
	}

	private String statusForChat(HelpSessionStatus status) {
		if (status == null) {
			return "pending";
		}
		return status.name().toLowerCase();
	}

	private String formatRelativeTime(LocalDateTime createdAt) {
		if (createdAt == null) {
			return "";
		}
		Duration duration = Duration.between(createdAt, LocalDateTime.now());
		long minutes = duration.toMinutes();
		if (minutes < 1) {
			return "Just now";
		}
		if (minutes < 60) {
			return minutes + " min ago";
		}
		long hours = duration.toHours();
		if (hours < 24) {
			return hours + " hr ago";
		}
		// For messages older than 24 hours, show actual time instead of "Yesterday"
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
		return createdAt.format(timeFormatter);
	}

	private String formatRelativeDate(LocalDateTime createdAt) {
		if (createdAt == null) {
			return "";
		}
		LocalDate messageDate = createdAt.toLocalDate();
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
		
		if (messageDate.equals(today)) {
			return "Today";
		}
		if (messageDate.equals(yesterday)) {
			return "Yesterday";
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
		return messageDate.format(formatter);
	}

	private double waitOpacity(LocalDateTime createdAt, LocalDateTime oldest) {
		if (createdAt == null) {
			return 0.25;
		}
		long ageMinutes = Math.max(0, Duration.between(createdAt, LocalDateTime.now()).toMinutes());
		long oldestMinutes = Math.max(1, Duration.between(oldest, LocalDateTime.now()).toMinutes());
		double ratio = Math.min(1.0, (double) ageMinutes / oldestMinutes);
		return Math.max(0.15, Math.min(0.95, 0.15 + ratio * 0.8));
	}

	public ChatSessionDto getChatSession(String sessionId, String email) {
		UserProfile currentUser = userService.getProfile(email);
		HelpSession session = sessionRepo.findByIdWithDetails(sessionId)
				.orElseThrow(() -> new UserNotFoundException("Session not found"));

		// Authorization check - user must be either requester or helper
		if (!currentUser.getId().equals(session.getRequester().getId()) 
				&& (session.getHelper() == null || !currentUser.getId().equals(session.getHelper().getId()))) {
			throw new UserNotFoundException("Access denied");
		}

		ChatSessionDto dto = new ChatSessionDto();
		dto.setSessionId(session.getSessionId());
		dto.setSubject(session.getSubject());
		dto.setSkillName(session.getSkill() != null ? session.getSkill().getName() : "General");
		dto.setStatus(statusForChat(session.getStatus()));
		dto.setRequesterName(session.getRequester().getUser() != null ? session.getRequester().getUser().getName() : "Unknown");
		dto.setRequesterProfilePicturepath(session.getRequester().getProfilePicturepath());
		dto.setHelperName(session.getHelper() != null && session.getHelper().getUser() != null ? session.getHelper().getUser().getName() : "Unknown");
		dto.setHelperProfilePicturepath(session.getHelper() != null ? session.getHelper().getProfilePicturepath() : null);
		dto.setMyRole(currentUser.getId().equals(session.getRequester().getId()) ? "requester" : "helper");
		dto.setSessionExpiresAt(session.getSessionExpiresAt());
		if (session.getSessionExpiresAt() != null) {
			dto.setSessionExpiresAtMs(session.getSessionExpiresAt().atZone(java.time.ZoneId.of("UTC")).toInstant().toEpochMilli());
		}
		dto.setExpiredReason(session.getExpiredReason());

		// Load messages and map to DTOs
		Pageable pageable = PageRequest.of(0, 1000);
		List<Message> messages = messageRepo.findBySession_SessionIdOrderBySentAtAsc(sessionId, pageable);
		List<ChatMessageDto> messageDtos = new ArrayList<>();
		
		for (Message msg : messages) {
			ChatMessageDto msgDto = new ChatMessageDto();
			msgDto.setMessageId(msg.getMessageId());
			msgDto.setText(msg.getMessage());
			msgDto.setTimeLabel(formatRelativeTime(msg.getSentAt()));
			msgDto.setDateLabel(formatRelativeDate(msg.getSentAt()));
			msgDto.setSenderName(msg.getSender().getUser() != null ? msg.getSender().getUser().getName() : "Unknown");
			msgDto.setFromHelper(session.getHelper() != null && msg.getSender().getId().equals(session.getHelper().getId()));
			msgDto.setMine(msg.getSender().getId().equals(currentUser.getId()));
			messageDtos.add(msgDto);
		}
		dto.setMessages(messageDtos);

		// Set showRatingPopup - only for requester on completed/expired sessions that haven't been rated
		boolean isRequester = "requester".equals(dto.getMyRole());
		boolean sessionDone = session.getStatus() == HelpSessionStatus.COMPLETED || session.getStatus() == HelpSessionStatus.EXPIRED;
		dto.setShowRatingPopup(isRequester && sessionDone && session.getRatedAt() == null);

		return dto;
	}

	@Transactional
	public void postMessage(String sessionId, String text, String email) {
		if (text == null || text.isBlank()) {
			throw new IllegalArgumentException("Message cannot be blank");
		}
		if (text.length() > 1000) {
			throw new IllegalArgumentException("Message cannot exceed 1000 characters");
		}

		UserProfile currentUser = userService.getProfile(email);
		HelpSession session = sessionRepo.findByIdWithDetails(sessionId)
				.orElseThrow(() -> new UserNotFoundException("Session not found"));

		// Authorization check
		if (!currentUser.getId().equals(session.getRequester().getId()) 
				&& (session.getHelper() == null || !currentUser.getId().equals(session.getHelper().getId()))) {
			throw new UserNotFoundException("Access denied");
		}

		// Check if session is expired or ignored before allowing message
		LocalDateTime now = LocalDateTime.now();
		if (session.getStatus() == HelpSessionStatus.EXPIRED || session.getStatus() == HelpSessionStatus.IGNORED) {
			throw new IllegalStateException("Session has expired: " + session.getExpiredReason());
		}

		// Check PENDING expiry (22 hours from creation)
		if (session.getStatus() == HelpSessionStatus.PENDING) {
			if (now.isAfter(session.getCreatedAt().plusHours(22))) {
				self.expireSession(session, HelpSessionStatus.IGNORED, "Expert did not respond within 22 hours");
				throw new IllegalStateException("Session has expired: Expert did not respond within 22 hours");
			}
		}

		// Check ACTIVE expiry (22 minutes from first expert reply)
		if (session.getStatus() == HelpSessionStatus.ACTIVE && session.getSessionExpiresAt() != null) {
			if (now.isAfter(session.getSessionExpiresAt())) {
				self.expireSession(session, HelpSessionStatus.EXPIRED, "Session time completed");
				throw new IllegalStateException("Session has expired: Session time completed");
			}
		}

		Message message = new Message();
		message.setMessage(text.trim());
		message.setRead(false);
		message.setSender(currentUser);
		message.setSession(session);
		message.setType(MessageType.Text);
		message.setSentAt(now);

		// Status transition logic: if expert sends first message, transition to ACTIVE
		boolean isFirstExpertReply = session.getHelper() != null && currentUser.getId().equals(session.getHelper().getId())
				&& session.getFirstExpertReplyAt() == null;
		if (isFirstExpertReply) {
			session.setFirstExpertReplyAt(now);
			session.setStatus(HelpSessionStatus.ACTIVE);
			session.setSessionExpiresAt(now.plusMinutes(22));
			session.setStartedAt(now);
		}

		messageRepo.save(message);
		sessionRepo.save(session);

		// Capture variables for push notification after transaction commits
		final UserProfile recipient = resolveRecipient(session, currentUser);

		if (recipient != null) {
			String senderName = currentUser.getUser() != null ? currentUser.getUser().getName() : "Someone";
			String messagePreview = text.trim().length() > 50 ? text.trim().substring(0, 47) + "..." : text.trim();
			String sessionIdValue = session.getSessionId();
			boolean isRequestAccepted = isFirstExpertReply;

			// Register push notification to fire after transaction commits
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					String title = isRequestAccepted ? "Request Accepted" : "New Message";
					String body = isRequestAccepted
							? senderName + " accepted your help request"
							: senderName + ": " + messagePreview;
					pushNotificationService.notifyUser(
							recipient,
							title,
							body,
							"/chat/" + sessionIdValue
					);
				}
			});
		}
	}

	@Transactional
	public void completeSession(String sessionId, String email) {
		UserProfile currentUser = userService.getProfile(email);
		HelpSession session = sessionRepo.findByIdWithDetails(sessionId)
				.orElseThrow(() -> new UserNotFoundException("Session not found"));

		// Authorization check - only helper can complete the session
		if (session.getHelper() == null || !currentUser.getId().equals(session.getHelper().getId())) {
			throw new UserNotFoundException("Only the helper can complete this session");
		}

		// Only active sessions can be completed
		if (session.getStatus() != HelpSessionStatus.ACTIVE) {
			throw new IllegalStateException("Only active sessions can be completed");
		}

		session.setStatus(HelpSessionStatus.COMPLETED);
		session.setEndedAt(LocalDateTime.now());
		sessionRepo.save(session);
	}

	@Transactional
	public void deleteSession(String sessionId, String email) {
		UserProfile currentUser = userService.getProfile(email);
		HelpSession session = sessionRepo.findByIdWithDetails(sessionId)
				.orElseThrow(() -> new UserNotFoundException("Session not found"));

		// Authorization check - only requester can delete their own session
		if (!currentUser.getId().equals(session.getRequester().getId())) {
			throw new UserNotFoundException("Only the requester can delete this session");
		}

		hardDeleteSession(session);
	}

	private void hardDeleteSession(HelpSession session) {
		// Delete all messages associated with the session
		Pageable pageable = PageRequest.of(0, 1000);
		List<Message> messages = messageRepo.findBySession_SessionIdOrderBySentAtAsc(session.getSessionId(), pageable);
		messageRepo.deleteAll(messages);

		// Delete the rating if it exists
		ratingRepo.findBySession_SessionId(session.getSessionId()).ifPresent(rating -> ratingRepo.delete(rating));

		// Delete the session
		sessionRepo.delete(session);
	}

	private UserProfile resolveRecipient(HelpSession session, UserProfile currentUser) {
		if (currentUser.getId().equals(session.getRequester().getId()) && session.getHelper() != null) {
			return session.getHelper();
		} else if (session.getHelper() != null && currentUser.getId().equals(session.getHelper().getId())) {
			return session.getRequester();
		}
		return null;
	}

	@Transactional
	public void purgeIgnoredSessions() {
		LocalDateTime cutoff = LocalDateTime.now().minusDays(22);
		Pageable pageable = PageRequest.of(0, 100);
		List<HelpSession> staleIgnoredSessions = sessionRepo.findByStatusAndEndedAtBefore(HelpSessionStatus.IGNORED, cutoff, pageable);
		
		for (HelpSession session : staleIgnoredSessions) {
			hardDeleteSession(session);
		}
	}

}
