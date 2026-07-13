package com.knowly.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.knowly.entity.User;
import com.knowly.entity.UserProfile;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfile, String> {

    public Optional<UserProfile> findByUser(User user);

    @Query("SELECT p FROM UserProfile p WHERE p.user.id != :userId ORDER BY p.user.createdAt DESC")
    List<UserProfile> findRecentProfiles(@Param("userId") String userId, Pageable pageable);
}
