package com.knowly.mapper;

import com.knowly.dto.ProjectDto;
import com.knowly.entity.Project;

public class ProjectMapper {

    public static Project toProject(ProjectDto dto) {
        Project project = new Project();
        project.setTitle(dto.getTitle());
        project.setRole(dto.getRole());
        project.setTechnologies(dto.getTechnologies());
        project.setLink(dto.getLink());
        project.setDescription(dto.getDescription());
        return project;
    }

    public static void updateProject(Project project, ProjectDto dto) {
        project.setTitle(dto.getTitle());
        project.setRole(dto.getRole());
        project.setTechnologies(dto.getTechnologies());
        project.setLink(dto.getLink());
        project.setDescription(dto.getDescription());
    }
}
