package com.knowly.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowly.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String>{

	Optional<Rating> findBySession_SessionId(String sessionId);

	@Query("SELECT AVG(r.stars) FROM Rating r WHERE r.session.skill.id = :skillId")
	Double findAverageStarsBySkillId(@Param("skillId") String skillId);
}
