package com.debugger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/breakpoints")
public class BreakpointController {

    @PostMapping
    public ResponseEntity<String> addBreakpoint(@RequestBody Map<String, Object> body) {
        String className = (String) body.get("className");
        Integer line = (Integer) body.get("line");

        if (className == null || line == null) {
            return ResponseEntity.badRequest().body("className and line are required");
        }

        // TODO: Hook into debugger engine
        return ResponseEntity.ok("Breakpoint added: " + className + ":" + line);
    }
}
