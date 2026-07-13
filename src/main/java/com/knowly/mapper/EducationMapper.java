package com.knowly.mapper;

import com.knowly.dto.EducationDto;
import com.knowly.entity.Education;

public class EducationMapper {

    public static Education toEducation(EducationDto dto) {
        Education education = new Education();
        education.setInstitution(dto.getInstitution());
        education.setDegree(dto.getDegree());
        education.setFieldOfStudy(dto.getFieldOfStudy());
        education.setStartYear(dto.getStartYear());
        education.setEndYear(dto.getEndYear());
        education.setDescription(dto.getDescription());
        return education;
    }

    public static void updateEducation(Education education, EducationDto dto) {
        education.setInstitution(dto.getInstitution());
        education.setDegree(dto.getDegree());
        education.setFieldOfStudy(dto.getFieldOfStudy());
        education.setStartYear(dto.getStartYear());
        education.setEndYear(dto.getEndYear());
        education.setDescription(dto.getDescription());
    }
}
