package com.debugger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @PostMapping
    public ResponseEntity<String> createSession() {
        // Generate a new session ID (stubbed for now)
        String sessionId = UUID.randomUUID().toString();
        return ResponseEntity.ok(sessionId);
    }
}
