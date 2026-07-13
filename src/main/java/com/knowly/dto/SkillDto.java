package com.knowly.dto;

import com.knowly.enums.ProficiencyLevel;
public class SkillDto {

	private String id;

	private String name;

	private double yearsOfExperience;

	private  ProficiencyLevel proficiencyLevel;

	@Override
	public String toString() {
		return "SkillDto [id=" + id + ", name=" + name + ", yearsOfExperience=" + yearsOfExperience + ", proficiencyLevel="
				+ proficiencyLevel + "]";
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
}
