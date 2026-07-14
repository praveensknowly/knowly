package com.knowly.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

import com.knowly.enums.Gender;

public class EditDto {
	private LocalDate dob;
	private String bio;
	private Gender gender;
	private String location;
	private List<String> languages = new ArrayList<>();
	private MultipartFile profilePicture;
	private String currentPassword;
	private String newEmail;
	private String newPassword;
	private String confirmPassword;
	private String number;

	public List<String> getLanguages() {
		return languages == null ? Collections.emptyList() : languages;
	}

	public void setLanguages(List<String> languages) {
		this.languages = languages == null ? new ArrayList<>() : new ArrayList<>(languages);
	}

	public void setLanguages(String languagesCsv) {
		if (languagesCsv == null || languagesCsv.isBlank()) {
			this.languages = new ArrayList<>();
			return;
		}

		this.languages = Arrays.stream(languagesCsv.split(","))
				.map(String::trim)
				.filter(value -> !value.isBlank())
				.distinct()
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public String toString() {
		return "EditDto [dob=" + dob + ", bio=" + bio + ", gender=" + gender + ", location=" + location + ", languages="
				+ languages + ", profilePicture=" + profilePicture + ", currentPassword=" + currentPassword + ", newEmail="
				+ newEmail + ", newPassword=" + newPassword + ", confirmPassword=" + confirmPassword + ", number=" + number
				+ "]";
	}

	public MultipartFile getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(MultipartFile profilePicture) {
		this.profilePicture = profilePicture;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewEmail() {
		return newEmail;
	}

	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
