package com.knowly.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knowly.entity.HelpSession;
import com.knowly.entity.Rating;
import com.knowly.entity.Skill;
import com.knowly.enums.HelpSessionStatus;
import com.knowly.exceptions.UserNotFoundException;
import com.knowly.repository.HelpSessionRepository;
import com.knowly.repository.RatingRepository;
import com.knowly.repository.SkillRepository;

@Service
public class RatingService {
	private final HelpSessionRepository sessionRepo;
	private final RatingRepository ratingRepo;
	private final SkillRepository skillRepo;
	private final UserService userService;

	public RatingService(HelpSessionRepository sessionRepo, RatingRepository ratingRepo,
			SkillRepository skillRepo, UserService userService) {
		this.sessionRepo = sessionRepo;
		this.ratingRepo = ratingRepo;
		this.skillRepo = skillRepo;
		this.userService = userService;
	}

	@Transactional
	public void submitRating(String sessionId, Integer stars, String review, String email) {
		// Load session
		HelpSession session = sessionRepo.findByIdWithDetails(sessionId)
				.orElseThrow(() -> new UserNotFoundException("Session not found"));

		// Authorize: only requester can rate
		var currentUser = userService.getProfile(email);
		if (!currentUser.getId().equals(session.getRequester().getId())) {
			throw new IllegalStateException("Only the requester can rate this session");
		}

		// Validate session status - only COMPLETED or EXPIRED can be rated
		if (session.getStatus() != HelpSessionStatus.COMPLETED && session.getStatus() != HelpSessionStatus.EXPIRED) {
			throw new IllegalStateException("Only completed or expired sessions can be rated");
		}

		// Validate not already rated
		if (session.getRatedAt() != null) {
			throw new IllegalStateException("This session has already been rated");
		}

		// Validate stars range
		if (stars == null || stars < 1 || stars > 5) {
			throw new IllegalArgumentException("Stars must be between 1 and 5");
		}

		// Validate review length
		if (review != null && review.length() > 1000) {
			throw new IllegalArgumentException("Review must be 1000 characters or less");
		}

		// Create and save rating
		Rating rating = new Rating();
		rating.setSession(session);
		rating.setStars(stars);
		rating.setReview(review);
		ratingRepo.save(rating);

		// Mark session as rated
		session.setRatedAt(LocalDateTime.now());
		sessionRepo.save(session);

		// Recompute skill score
		if (session.getSkill() != null) {
			Double avgStars = ratingRepo.findAverageStarsBySkillId(session.getSkill().getId());
			Skill skill = skillRepo.findById(session.getSkill().getId())
					.orElseThrow(() -> new RuntimeException("Skill not found"));
			skill.setSkillScore(avgStars != null ? avgStars : 0.0);
			skillRepo.save(skill);
		}
	}
}
