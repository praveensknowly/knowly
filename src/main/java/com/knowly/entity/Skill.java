package com.knowly.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.knowly.enums.ProficiencyLevel;

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

import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
    indexes = {
        @Index(name = "idx_skill_name", columnList = "name"),
        @Index(name = "idx_skill_search_key", columnList = "searchKey"),
        @Index(name = "idx_skill_score", columnList = "skillScore")
    }
)
public class Skill {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "profile_id",nullable = false)
	private UserProfile userProfile;
	@Column(nullable = false, length = 100)
	private String name;
	private String searchKey;
	private double yearsOfExperience;
	@Enumerated(EnumType.STRING)
	private  ProficiencyLevel proficiencyLevel;
	private double skillScore;
	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public UserProfile getUserProfile() {
		return userProfile;
	}
	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getYearsOfExperience() {
		return yearsOfExperience;
	}
	public void setYearsOfExperience(double yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}
	public ProficiencyLevel getProficiencyLevel() {
		return proficiencyLevel;
	}
	public void setProficiencyLevel(ProficiencyLevel proficiencyLevel) {
		this.proficiencyLevel = proficiencyLevel;
	}
	public double getSkillScore() {
		return skillScore;
	}
	public void setSkillScore(double skillScore) {
		this.skillScore = skillScore;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	@Override
	public String toString() {
		return "Skill [id=" + id + ", name=" + name + ", yearsOfExperience="
				+ yearsOfExperience + ", proficiencyLevel=" + proficiencyLevel + ", skillScore=" + skillScore
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
	public String getSearchKey() {
		return searchKey;
	}
	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}
	
}
