package com.knowly.entity;



import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
@Entity
public class Rating {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String ratingId;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="session_id")
	private HelpSession session;
	@Min(1)
	@Max(5)
	private Integer stars;
	@Column(length=1000)
	private String review;
	@CreationTimestamp
	private LocalDateTime createdAt;
	@Override
	public String toString() {
		return "Rating [ratingId=" + ratingId + ", stars=" + stars + ", review=" + review + ", createdAt=" + createdAt
				+ "]";
	}
	public String getRatingId() {
		return ratingId;
	}
	public void setRatingId(String ratingId) {
		this.ratingId = ratingId;
	}
	public HelpSession getSession() {
		return session;
	}
	public void setSession(HelpSession session) {
		this.session = session;
	}
	public Integer getStars() {
		return stars;
	}
	public void setStars(Integer stars) {
		this.stars = stars;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
}

