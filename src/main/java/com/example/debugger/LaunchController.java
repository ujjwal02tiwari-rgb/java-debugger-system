package com.debugger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/launch")
public class LaunchController {

    @PostMapping
    public ResponseEntity<String> launchTarget(@RequestBody Map<String, String> body) {
        String target = body.get("target");
        if (target == null || target.isBlank()) {
            return ResponseEntity.badRequest().body("Target class is required");
        }
        // TODO: Implement actual JVM launch here
        return ResponseEntity.ok("Launched target: " + target);
    }
}
