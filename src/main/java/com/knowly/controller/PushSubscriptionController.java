package com.knowly.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.knowly.dto.PushSubscriptionDto;
import com.knowly.entity.PushSubscription;
import com.knowly.entity.UserProfile;
import com.knowly.repository.PushSubscriptionRepository;
import com.knowly.service.UserService;

@RestController
public class PushSubscriptionController {

	private final PushSubscriptionRepository subscriptionRepo;
	private final UserService userService;

	@Value("${vapid.public.key:}")
	private String vapidPublicKey;

	public PushSubscriptionController(PushSubscriptionRepository subscriptionRepo, UserService userService) {
		this.subscriptionRepo = subscriptionRepo;
		this.userService = userService;
	}

	@GetMapping("/push/vapid-public-key")
	@ResponseBody
	public Map<String, String> vapidPublicKey() {
		return Map.of("publicKey", vapidPublicKey == null ? "" : vapidPublicKey);
	}

	@PostMapping("/push/subscribe")
	@ResponseBody
	public Map<String, Boolean> subscribe(@RequestBody PushSubscriptionDto dto, Authentication auth) {
		UserProfile profile = userService.getProfile(auth.getName());

		PushSubscription sub = subscriptionRepo.findByEndpoint(dto.getEndpoint())
				.orElseGet(PushSubscription::new);
		sub.setUserProfile(profile);
		sub.setEndpoint(dto.getEndpoint());
		sub.setP256dhKey(dto.getKeys().getP256dh());
		sub.setAuthKey(dto.getKeys().getAuth());
		subscriptionRepo.save(sub);

		return Map.of("success", true);
	}

	@PostMapping("/push/unsubscribe")
	@ResponseBody
	public Map<String, Boolean> unsubscribe(@RequestBody Map<String, String> body, Authentication auth) {
		String endpoint = body.get("endpoint");
		if (endpoint != null) {
			UserProfile profile = userService.getProfile(auth.getName());
			subscriptionRepo.findByEndpoint(endpoint).ifPresent(sub -> {
				if (sub.getUserProfile().getId().equals(profile.getId())) {
					subscriptionRepo.deleteByEndpoint(endpoint);
				}
			});
		}
		return Map.of("success", true);
	}
}
