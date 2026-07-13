package com.knowly.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knowly.dto.ProjectDto;
import com.knowly.entity.Project;
import com.knowly.entity.User;
import com.knowly.exceptions.InvalidProjectException;
import com.knowly.exceptions.UserNotFoundException;
import com.knowly.mapper.ProjectMapper;
import com.knowly.repository.ProjectRepository;
import com.knowly.repository.UserRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepo;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepo) {
        this.projectRepository = projectRepository;
        this.userRepo = userRepo;
    }

    public Set<Project> findAll(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not Found"));
        return projectRepository.findByUserProfile(user.getProfile());
    }

    @Transactional
    public void save(ProjectDto dto, String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not Found"));

        if (dto.getTitle() == null || dto.getTitle().isBlank() || dto.getRole() == null || dto.getRole().isBlank()) {
            throw new InvalidProjectException("Project title and role are required.");
        }

        Project project;
        if (dto.getId() != null && !dto.getId().isBlank()) {
            project = projectRepository.findById(dto.getId())
                    .orElseThrow(() -> new InvalidProjectException("Project not found."));
            if (!project.getUserProfile().getId().equals(user.getProfile().getId())) {
                throw new InvalidProjectException("Invalid project record.");
            }
            ProjectMapper.updateProject(project, dto);
        } else {
            project = ProjectMapper.toProject(dto);
            project.setUserProfile(user.getProfile());
        }

        projectRepository.save(project);
    }

    @Transactional
    public void deleteById(String id, String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not Found"));
        Project project = projectRepository.findById(id).orElseThrow(() -> new InvalidProjectException("Project not found."));
        if (!project.getUserProfile().getId().equals(user.getProfile().getId())) {
            throw new InvalidProjectException("Invalid project record.");
        }
        projectRepository.delete(project);
    }
}
