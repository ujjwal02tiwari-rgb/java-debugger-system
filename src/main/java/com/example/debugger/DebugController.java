package com.example.debugger.controller;

import com.example.debugger.dto.LaunchRequest;
import com.example.debugger.dto.BreakpointRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class DebugController {

    @PostMapping("/sessions")
    public ResponseEntity<Map<String, String>> createSession() {
        Map<String, String> response = new HashMap<>();
        response.put("sessionId", UUID.randomUUID().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/launch")
    public ResponseEntity<Map<String, String>> launch(@RequestBody LaunchRequest req) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Launched target: " + req.getMainClass());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/breakpoints")
    public ResponseEntity<Map<String, String>> addBreakpoint(@RequestBody BreakpointRequest req) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Breakpoint added: " + req.getClassName() + ":" + req.getLine());
        return ResponseEntity.ok(response);
    }
}
