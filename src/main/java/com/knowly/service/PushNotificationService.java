package com.knowly.service;

import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.knowly.entity.PushSubscription;
import com.knowly.entity.UserProfile;
import com.knowly.repository.PushSubscriptionRepository;

import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Service
public class PushNotificationService {

	private static final Logger logger = LoggerFactory.getLogger(PushNotificationService.class);

	private final PushSubscriptionRepository subscriptionRepo;

	@Value("${vapid.public.key:}")
	private String vapidPublicKey;

	@Value("${vapid.private.key:}")
	private String vapidPrivateKey;

	@Value("${vapid.subject:mailto:support@praveensknowly.in}")
	private String vapidSubject;

	private PushService pushService;

	public PushNotificationService(PushSubscriptionRepository subscriptionRepo) {
		this.subscriptionRepo = subscriptionRepo;
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

	/**
	 * Sends a push notification to every browser subscription registered by this user.
	 * Silently skips if push isn't configured. Removes subscriptions the push
	 * service reports as dead (410 Gone / 404 Not Found).
	 */
	public void notifyUser(UserProfile profile, String title, String body, String url) {
		if (pushService == null || profile == null) {
			return;
		}

		List<PushSubscription> subs = subscriptionRepo.findByUserProfile(profile);
		if (subs.isEmpty()) {
			return;
		}

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
