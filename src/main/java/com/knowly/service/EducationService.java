package com.knowly.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knowly.dto.EducationDto;
import com.knowly.entity.Education;
import com.knowly.entity.User;
import com.knowly.exceptions.InvalidEducationException;
import com.knowly.exceptions.UserNotFoundException;
import com.knowly.mapper.EducationMapper;
import com.knowly.repository.EducationRepository;
import com.knowly.repository.UserRepository;

@Service
public class EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepo;

    public EducationService(EducationRepository educationRepository, UserRepository userRepo) {
        this.educationRepository = educationRepository;
        this.userRepo = userRepo;
    }

    public Set<Education> findAll(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not Found"));
        return educationRepository.findByUserProfile(user.getProfile());
    }

    @Transactional
    public void save(EducationDto dto, String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not Found"));

        if (dto.getInstitution() == null || dto.getInstitution().isBlank() || dto.getDegree() == null || dto.getDegree().isBlank()) {
            throw new InvalidEducationException("Institution and degree are required.");
        }

        Education education;
        if (dto.getId() != null && !dto.getId().isBlank()) {
            education = educationRepository.findById(dto.getId())
                    .orElseThrow(() -> new InvalidEducationException("Education entry not found."));
            if (!education.getUserProfile().getId().equals(user.getProfile().getId())) {
                throw new InvalidEducationException("Invalid education record.");
            }
            EducationMapper.updateEducation(education, dto);
        } else {
            education = EducationMapper.toEducation(dto);
            education.setUserProfile(user.getProfile());
        }

        educationRepository.save(education);
    }

    @Transactional
    public void deleteById(String id, String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not Found"));
        Education education = educationRepository.findById(id).orElseThrow(() -> new InvalidEducationException("Education entry not found."));
        if (!education.getUserProfile().getId().equals(user.getProfile().getId())) {
            throw new InvalidEducationException("Invalid education record.");
        }
        educationRepository.delete(education);
    }
}
