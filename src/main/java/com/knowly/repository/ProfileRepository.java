package com.knowly.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import com.knowly.entity.User;
import com.knowly.entity.UserProfile;

@Repository
public interface ProfileRepository extends JpaRepository<UserProfile, String> {

    public Optional<UserProfile> findByUser(User user);

    @Query("SELECT p FROM UserProfile p WHERE p.user.id != :userId ORDER BY p.user.createdAt DESC")
    List<UserProfile> findRecentProfiles(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT DISTINCT p FROM UserProfile p WHERE p.user.id != :userId AND " +
           "(LOWER(p.user.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.user.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<UserProfile> searchByNameOrEmail(@Param("query") String query, @Param("userId") String userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE UserProfile p SET p.lastActiveAt = :now WHERE p.id = :profileId")
    void touchLastActive(@Param("profileId") String profileId, @Param("now") LocalDateTime now);
}
