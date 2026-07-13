package com.knowly.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.knowly.entity.Certification;
import com.knowly.repository.CertificationRepository;
import com.knowly.repository.UserRepository;

@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final CertificationRepository certRepo;

    public DataInitializer(UserRepository userRepo, CertificationRepository certRepo) {
        this.userRepo = userRepo;
        this.certRepo = certRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed a sample certification for the first user that has a profile, if none exist
        userRepo.findAll().stream()
            .filter(user -> user.getProfile() != null)
            .findFirst()
            .ifPresent(user -> {
                if (certRepo.findByUserProfile(user.getProfile()).isEmpty()) {
                    Certification c = new Certification();
                    c.setUserProfile(user.getProfile());
                    c.setName("Sample Certification");
                    c.setIssuer("Knowly Academy");
                    c.setYear(2024);
                    c.setCredentialUrl("https://example.com/cert/sample");
                    certRepo.save(c);
                }
            });
    }
}
