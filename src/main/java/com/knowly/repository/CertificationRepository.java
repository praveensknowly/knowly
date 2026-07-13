package com.knowly.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.knowly.entity.Certification;
import com.knowly.entity.UserProfile;

public interface CertificationRepository extends JpaRepository<Certification, String> {
    Set<Certification> findByUserProfile(UserProfile profile);
}
