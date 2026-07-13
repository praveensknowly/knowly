package com.knowly.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowly.entity.Project;
import com.knowly.entity.UserProfile;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    Set<Project> findByUserProfile(UserProfile profile);
}
