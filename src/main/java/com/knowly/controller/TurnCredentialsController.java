package com.knowly.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowly.service.TurnCredentialService;

@RestController
public class TurnCredentialsController {

    private final TurnCredentialService turnCredentialService;

    public TurnCredentialsController(TurnCredentialService turnCredentialService) {
        this.turnCredentialService = turnCredentialService;
    }

    @GetMapping("/api/turn-credentials")
    public ResponseEntity<TurnCredentialService.TurnCredentials> getTurnCredentials(Principal principal) {
        return ResponseEntity.ok(turnCredentialService.generate(principal.getName()));
    }
}
