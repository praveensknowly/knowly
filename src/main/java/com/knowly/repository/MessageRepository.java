package com.knowly.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.knowly.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, String>{

	List<Message> findBySession_SessionIdOrderBySentAtAsc(String sessionId, Pageable pageable);
}
