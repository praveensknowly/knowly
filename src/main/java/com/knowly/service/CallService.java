package com.knowly.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.knowly.entity.Call;
import com.knowly.entity.HelpSession;
import com.knowly.entity.UserProfile;
import com.knowly.enums.CallStatus;
import com.knowly.exceptions.UserNotFoundException;
import com.knowly.repository.CallRepository;
import com.knowly.repository.HelpSessionRepository;

@Service
public class CallService {

    private final CallRepository callRepo;
    private final HelpSessionRepository sessionRepo;
    private final UserService userService;
    private final PushNotificationService pushNotificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public CallService(CallRepository callRepo, HelpSessionRepository sessionRepo, UserService userService,
                       PushNotificationService pushNotificationService, SimpMessagingTemplate messagingTemplate) {
        this.callRepo = callRepo;
        this.sessionRepo = sessionRepo;
        this.userService = userService;
        this.pushNotificationService = pushNotificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Call startCall(String helpSessionId, String callerEmail) {
        HelpSession session = sessionRepo.findByIdWithDetails(helpSessionId)
                .orElseThrow(() -> new UserNotFoundException("Session not found"));
        UserProfile caller = userService.getProfile(callerEmail);
        UserProfile callee = session.getRequester().getId().equals(caller.getId())
                ? session.getHelper() : session.getRequester();

        // authorization: caller must be a participant
        if (!caller.getId().equals(session.getRequester().getId()) &&
            !caller.getId().equals(session.getHelper().getId())) {
            throw new IllegalArgumentException("Not a participant of this session");
        }

        Call call = new Call();
        call.setHelpSession(session);
        call.setCaller(caller);
        call.setCallee(callee);
        call.setStatus(CallStatus.RINGING);
        call.setInitiatedAt(LocalDateTime.now());
        callRepo.save(call);

        // Ring the other party over the signaling channel
        messagingTemplate.convertAndSendToUser(
            callee.getUser().getEmail(), "/queue/signal",
            Map.of("type", "incoming-call", "callId", call.getId(),
                   "sessionId", helpSessionId, "fromName", caller.getUser().getName())
        );

        // Reuse your existing push+email fallback for offline callees
        pushNotificationService.notifyUser(callee, "Incoming Call",
                caller.getUser().getName() + " is calling you", "/chat/" + helpSessionId);

        return call;
    }

    @Transactional
    public void markConnected(String callId) {
        Call call = callRepo.findById(callId).orElseThrow();
        call.markConnected();
        callRepo.save(call);
    }

    @Transactional
    public void endCall(String callId) {
        Call call = callRepo.findById(callId).orElseThrow();
        call.markEnded();
        callRepo.save(call);
    }
}
