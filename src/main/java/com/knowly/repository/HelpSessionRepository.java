package com.knowly.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.knowly.entity.HelpSession;
import com.knowly.enums.HelpSessionStatus;

@Repository
public interface HelpSessionRepository extends JpaRepository<HelpSession, String>{

	@Query("""
			SELECT DISTINCT s FROM HelpSession s
			LEFT JOIN FETCH s.messages
			LEFT JOIN FETCH s.requester r
			LEFT JOIN FETCH r.user
			LEFT JOIN FETCH s.skill
			WHERE s.helper.id = :helperId
			ORDER BY s.createdAt DESC
			""")
	List<HelpSession> findByHelperIdWithDetails(@Param("helperId") String helperId);

	@Query("""
			SELECT DISTINCT s FROM HelpSession s
			LEFT JOIN FETCH s.messages
			LEFT JOIN FETCH s.helper h
			LEFT JOIN FETCH h.user
			LEFT JOIN FETCH s.skill
			WHERE s.requester.id = :requesterId
			ORDER BY s.createdAt DESC
			""")
	List<HelpSession> findByRequesterIdWithDetails(@Param("requesterId") String requesterId);

	@Query("""
	    SELECT s FROM HelpSession s
	    LEFT JOIN FETCH s.requester r LEFT JOIN FETCH r.user
	    LEFT JOIN FETCH s.helper h LEFT JOIN FETCH h.user
	    LEFT JOIN FETCH s.skill
	    WHERE s.sessionId = :sessionId
	    """)
	Optional<HelpSession> findByIdWithDetails(@Param("sessionId") String sessionId);

	List<HelpSession> findByStatusAndCreatedAtBefore(HelpSessionStatus status, LocalDateTime dateTime, Pageable pageable);
	List<HelpSession> findByStatusAndSessionExpiresAtBefore(HelpSessionStatus status, LocalDateTime dateTime, Pageable pageable);
	List<HelpSession> findByStatusAndEndedAtBefore(HelpSessionStatus status, LocalDateTime dateTime, Pageable pageable);
}
