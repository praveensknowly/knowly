package com.knowly.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	@Column(nullable = false)
	private String name;
	@Column(nullable=false, unique = true)
	private String email;
	@Column(unique = true)
	private String number;
	private String password;
	@Column(nullable = false)
	private String provider = "LOCAL";
	private String providerId;
	@CreationTimestamp
	private LocalDateTime createdAt;
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
	private UserProfile profile;
	
	@Column(nullable = false)
	private boolean emailVerified = false;
	public boolean isEmailVerified() {
	    return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
	    this.emailVerified = emailVerified;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", number=" + number
				+ ", createdAt=" + createdAt + ", profile=" + profile + "]";
	}
	
	
	public UserProfile getProfile() {
		return profile;
	}


	public void setProfile(UserProfile profile) {
		this.profile = profile;
	}


	
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
}
