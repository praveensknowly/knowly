package com.knowly.service;

import java.security.Security;
import java.time.LocalDateTime;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.knowly.entity.PushSubscription;
import com.knowly.entity.UserProfile;
import com.knowly.repository.ProfileRepository;
import com.knowly.repository.PushSubscriptionRepository;

import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Service
public class PushNotificationService {

	private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);
	private static final long ONLINE_THRESHOLD_SECONDS = 60;
	private static final long EMAIL_COOLDOWN_MINUTES = 10;

	private final PushSubscriptionRepository subscriptionRepo;
	private final EmailService emailService;
	private final ProfileRepository profileRepo;

	@Value("${vapid.public.key:}")
	private String vapidPublicKey;

	@Value("${vapid.private.key:}")
	private String vapidPrivateKey;

	@Value("${vapid.subject:mailto:support@praveensknowly.in}")
	private String vapidSubject;

	@Value("${app.base-url:http://localhost:8080}")
	private String baseUrl;

	private PushService pushService;

	public PushNotificationService(PushSubscriptionRepository subscriptionRepo, EmailService emailService, ProfileRepository profileRepo) {
		this.subscriptionRepo = subscriptionRepo;
		this.emailService = emailService;
		this.profileRepo = profileRepo;
	}

	@PostConstruct
	public void init() {
		Security.addProvider(new BouncyCastleProvider());
		if (vapidPublicKey == null || vapidPublicKey.isBlank()
				|| vapidPrivateKey == null || vapidPrivateKey.isBlank()) {
			logger.warn("VAPID keys not configured — push notifications are disabled.");
			return;
		}
		try {
			this.pushService = new PushService(vapidPublicKey, vapidPrivateKey, vapidSubject);
		} catch (Exception e) {
			logger.error("Failed to initialize PushService", e);
		}
	}

	private boolean isOnline(UserProfile profile) {
		LocalDateTime lastActive = profile.getLastActiveAt();
		return lastActive != null &&
			   lastActive.isAfter(LocalDateTime.now().minusSeconds(ONLINE_THRESHOLD_SECONDS));
	}

	private boolean shouldSendEmail(UserProfile profile) {
		LocalDateTime lastEmailed = profile.getLastEmailedAt();
		if (lastEmailed == null) {
			return true; // Never emailed before, send it
		}
		return lastEmailed.isBefore(LocalDateTime.now().minusMinutes(EMAIL_COOLDOWN_MINUTES));
	}

	/**
	 * Sends a push notification to every browser subscription registered by this user.
	 * Silently skips if push isn't configured. Removes subscriptions the push
	 * service reports as dead (410 Gone / 404 Not Found).
	 */
	public void notifyUser(UserProfile profile, String title, String body, String url) {
		if (profile == null) {
			return;
		}

		boolean online = isOnline(profile);

		// Always attempt push if configured and subscribed (best effort, cheap)
		if (pushService != null) {
			List<PushSubscription> subs = subscriptionRepo.findByUserProfile(profile);
			String payload = buildPayload(title, body, url);
			for (PushSubscription sub : subs) {
				try {
					Notification notification = new Notification(
							sub.getEndpoint(),
							sub.getP256dhKey(),
							sub.getAuthKey(),
							payload);
					var response = pushService.send(notification);
					int status = response.getStatusLine().getStatusCode();
					if (status == 404 || status == 410) {
						subscriptionRepo.deleteByEndpoint(sub.getEndpoint());
					}
				} catch (Exception e) {
					logger.error("Failed to send push notification to subscription {}", sub.getId(), e);
				}
			}
		}

		// Email fallback only if the user isn't actively on the site right now
		if (!online) {
			sendEmailFallback(profile, title, body, url);
		}
	}

	private void sendEmailFallback(UserProfile profile, String title, String body, String url) {
		if (profile.getUser() == null || profile.getUser().getEmail() == null) return;
		if (!shouldSendEmail(profile)) return;

		String absoluteUrl = baseUrl + url;
		emailService.sendNotificationEmail(profile.getUser().getEmail(), profile.getUser().getName(), title, body, absoluteUrl);

		// Update lastEmailedAt
		profile.setLastEmailedAt(LocalDateTime.now());
		profileRepo.save(profile);
	}

	private String buildPayload(String title, String body, String url) {
		return "{"
				+ "\"title\":\"" + escape(title) + "\","
				+ "\"body\":\"" + escape(body) + "\","
				+ "\"url\":\"" + escape(url) + "\""
				+ "}";
	}

	private String escape(String s) {
		if (s == null) return "";
		return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
	}
}
