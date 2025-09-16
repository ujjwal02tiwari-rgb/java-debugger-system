package com.example.debugger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.debugger.dto.LaunchRequest;
import com.example.debugger.dto.BreakpointRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    private final DebuggerService debugger;

    @Autowired
    public DebugController(DebuggerService debugger) {
        this.debugger = debugger;
    }

    @PostMapping("/session")
    public Map<String, String> createSession() throws Exception {
        DebugSession session = debugger.createSession();
        return Map.of("sessionId", session.getId());
    }

    @PostMapping("/session/{id}/launch")
    public void launch(@PathVariable("id") String id, @RequestBody LaunchRequest req) throws Exception {
        debugger.launch(id, req.getMainClass());
    }

    @PostMapping("/session/{id}/breakpoint")
    public void addBreakpoint(@PathVariable("id") String id, @RequestBody BreakpointRequest req) throws Exception {
        debugger.addBreakpoint(id, req.getClassName(), req.getLine());
    }
}
