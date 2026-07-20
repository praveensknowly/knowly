package com.knowly.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.knowly.entity.Call;
import com.knowly.repository.CallRepository;
import com.knowly.service.CallService;

@Controller
public class CallSignalingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CallService callService;
    private final CallRepository callRepo;

    public CallSignalingController(SimpMessagingTemplate messagingTemplate, CallService callService,
                                   CallRepository callRepo) {
        this.messagingTemplate = messagingTemplate;
        this.callService = callService;
        this.callRepo = callRepo;
    }

    @MessageMapping("/call/start")
    public void start(@Payload Map<String, String> payload, Principal principal) {
        callService.startCall(payload.get("sessionId"), principal.getName());
    }

    @MessageMapping("/call/offer")
    public void offer(@Payload Map<String, Object> payload, Principal principal) {
        relay(payload, principal, "offer");
    }

    @MessageMapping("/call/answer")
    public void answer(@Payload Map<String, Object> payload, Principal principal) {
        relay(payload, principal, "answer");
    }

    @MessageMapping("/call/ice-candidate")
    public void iceCandidate(@Payload Map<String, Object> payload, Principal principal) {
        relay(payload, principal, "ice-candidate");
    }

    @MessageMapping("/call/connected")
    public void connected(@Payload Map<String, String> payload) {
        callService.markConnected(payload.get("callId"));
    }

    @MessageMapping("/call/end")
    public void end(@Payload Map<String, String> payload, Principal principal) {
        Call call = callRepo.findById(payload.get("callId")).orElseThrow();
        callService.endCall(call.getId());
        String otherEmail = call.getCaller().getUser().getEmail().equals(principal.getName())
                ? call.getCallee().getUser().getEmail() : call.getCaller().getUser().getEmail();
        messagingTemplate.convertAndSendToUser(otherEmail, "/queue/signal",
                Map.of("type", "call-ended", "callId", call.getId()));
    }

    @MessageMapping("/call/ready")
    public void ready(@Payload Map<String, String> payload, Principal principal) {
        Call call = callRepo.findById(payload.get("callId")).orElseThrow();
        String callerEmail = call.getCaller().getUser().getEmail();
        String calleeEmail = call.getCallee().getUser().getEmail();

        // Only the callee should be sending "ready" — notify the caller
        if (principal.getName().equals(calleeEmail)) {
            messagingTemplate.convertAndSendToUser(callerEmail, "/queue/signal",
                    Map.of("type", "callee-ready", "callId", call.getId()));
        }
    }

    // Relays a message to the OTHER participant of the call, after verifying
    // the sender (principal) is actually one of the two participants.
    private void relay(Map<String, Object> payload, Principal principal, String type) {
        Call call = callRepo.findById((String) payload.get("callId")).orElseThrow();
        String callerEmail = call.getCaller().getUser().getEmail();
        String calleeEmail = call.getCallee().getUser().getEmail();

        if (!principal.getName().equals(callerEmail) && !principal.getName().equals(calleeEmail)) {
            return; // not a participant — drop silently
        }
        String targetEmail = principal.getName().equals(callerEmail) ? calleeEmail : callerEmail;

        Map<String, Object> out = new HashMap<>(payload);
        out.put("type", type);
        messagingTemplate.convertAndSendToUser(targetEmail, "/queue/signal", out);
    }
}
