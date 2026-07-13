package com.knowly.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.knowly.entity.Skill;
import com.knowly.entity.UserProfile;

@Repository
public interface SkillRepository extends JpaRepository<Skill, String> {

    public boolean existsByUserProfileAndSearchKey(UserProfile profile, String searchKey);

    public Set<Skill> findByUserProfile(UserProfile profile);

   public Page<Skill> findBySearchKeyContainingIgnoreCaseOrderBySkillScoreDesc(String keyword, Pageable pageable);

}
