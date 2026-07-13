package com.knowly.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knowly.entity.Certification;
import com.knowly.entity.User;
import com.knowly.entity.UserProfile;
import com.knowly.repository.CertificationRepository;
import com.knowly.repository.UserRepository;

@Service
public class CertificationService {
    private final CertificationRepository certRepo;
    private final UserRepository userRepo;

    public CertificationService(CertificationRepository certRepo, UserRepository userRepo) {
        this.certRepo = certRepo;
        this.userRepo = userRepo;
    }

    public Set<Certification> findAll(String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null || user.getProfile() == null) {
            return java.util.Collections.emptySet();
        }
        UserProfile profile = user.getProfile();
        return certRepo.findByUserProfile(profile);
    }

    @Transactional
    public void save(com.knowly.dto.CertificationDto dto, String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserProfile profile = user.getProfile();
        if (profile == null) throw new IllegalArgumentException("User profile not found");

        Certification cert;
        if (dto.getId() != null && !dto.getId().isEmpty()) {
            cert = certRepo.findById(dto.getId()).orElse(new Certification());
            if (cert.getUserProfile() != null && !cert.getUserProfile().getId().equals(profile.getId())) {
                throw new IllegalArgumentException("Invalid certification record.");
            }
        } else {
            cert = new Certification();
            cert.setUserProfile(profile);
        }

        cert.setName(dto.getName());
        cert.setIssuer(dto.getIssuer());
        cert.setYear(dto.getYear());
        cert.setCredentialUrl(dto.getCredentialUrl());

        certRepo.save(cert);
    }

    @Transactional
    public void deleteById(String id, String email) {
        Certification cert = certRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Certification not found"));
        User user = userRepo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getProfile() == null || !cert.getUserProfile().getId().equals(user.getProfile().getId())) {
            throw new IllegalArgumentException("Unauthorized");
        }
        certRepo.delete(cert);
    }
}
