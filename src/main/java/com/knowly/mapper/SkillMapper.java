package com.knowly.mapper;

import com.knowly.dto.SkillDto;
import com.knowly.entity.Skill;

public class SkillMapper {
	public static Skill toSkill(SkillDto dto) {
		Skill skill=new Skill();
		skill.setName(dto.getName());
		skill.setProficiencyLevel(dto.getProficiencyLevel());
		skill.setYearsOfExperience(dto.getYearsOfExperience());
		return skill;
	}
}
