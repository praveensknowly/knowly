package com.knowly.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowly.entity.Education;
import com.knowly.entity.UserProfile;

@Repository
public interface EducationRepository extends JpaRepository<Education, String> {

    Set<Education> findByUserProfile(UserProfile profile);
}
