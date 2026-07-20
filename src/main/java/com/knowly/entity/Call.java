package com.knowly.entity;

import java.time.Duration;
import java.time.LocalDateTime;

import com.knowly.enums.CallStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "call_session")
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "help_session_id", nullable = false)
    private HelpSession helpSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller_id", nullable = false)
    private UserProfile caller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "callee_id", nullable = false)
    private UserProfile callee;

    @Enumerated(EnumType.STRING)
    private CallStatus status;

    private LocalDateTime initiatedAt;
    private LocalDateTime connectedAt;
    private LocalDateTime endedAt;
    private Long durationSeconds;

    public void markConnected() {
        if (this.connectedAt == null) {
            this.connectedAt = LocalDateTime.now();
            this.status = CallStatus.CONNECTED;
        }
    }

    public void markEnded() {
        this.endedAt = LocalDateTime.now();
        this.status = CallStatus.ENDED;
        if (this.connectedAt != null) {
            this.durationSeconds = Duration.between(this.connectedAt, this.endedAt).getSeconds();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HelpSession getHelpSession() {
        return helpSession;
    }

    public void setHelpSession(HelpSession helpSession) {
        this.helpSession = helpSession;
    }

    public UserProfile getCaller() {
        return caller;
    }

    public void setCaller(UserProfile caller) {
        this.caller = caller;
    }

    public UserProfile getCallee() {
        return callee;
    }

    public void setCallee(UserProfile callee) {
        this.callee = callee;
    }

    public CallStatus getStatus() {
        return status;
    }

    public void setStatus(CallStatus status) {
        this.status = status;
    }

    public LocalDateTime getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(LocalDateTime initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public LocalDateTime getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(LocalDateTime connectedAt) {
        this.connectedAt = connectedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
