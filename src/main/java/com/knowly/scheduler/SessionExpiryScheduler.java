package com.knowly.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.knowly.entity.HelpSession;
import com.knowly.enums.HelpSessionStatus;
import com.knowly.repository.HelpSessionRepository;
import com.knowly.service.HelpSessionService;

@Component
public class SessionExpiryScheduler {

    private final HelpSessionRepository sessionRepo;
    private final HelpSessionService helpSessionService;

    public SessionExpiryScheduler(HelpSessionRepository sessionRepo, HelpSessionService helpSessionService) {
        this.sessionRepo = sessionRepo;
        this.helpSessionService = helpSessionService;
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void checkExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 100);
        
        // Find PENDING sessions that have exceeded 22 hours
        List<HelpSession> pendingToExpire = sessionRepo.findByStatusAndCreatedAtBefore(
            HelpSessionStatus.PENDING, 
            now.minusHours(22),
            pageable
        );
        
        for (HelpSession session : pendingToExpire) {
            expireOne(session.getSessionId(), HelpSessionStatus.IGNORED, "Expert did not respond within 22 hours", now);
        }
        
        // Find ACTIVE sessions that have exceeded their session expiry time
        List<HelpSession> activeToExpire = sessionRepo.findByStatusAndSessionExpiresAtBefore(
            HelpSessionStatus.ACTIVE, 
            now,
            pageable
        );
        
        for (HelpSession session : activeToExpire) {
            expireOne(session.getSessionId(), HelpSessionStatus.EXPIRED, "Session time completed", now);
        }
    }

    @Transactional
    public void expireOne(String sessionId, HelpSessionStatus status, String reason, LocalDateTime now) {
        sessionRepo.findById(sessionId).ifPresent(session -> {
            session.setStatus(status);
            session.setExpiredReason(reason);
            session.setEndedAt(now);
            sessionRepo.save(session);
        });
    }

    @Scheduled(cron = "0 0 3 * * *") // Run daily at 3 AM
    @Transactional
    public void purgeStaleIgnoredSessions() {
        helpSessionService.purgeIgnoredSessions();
    }
}
