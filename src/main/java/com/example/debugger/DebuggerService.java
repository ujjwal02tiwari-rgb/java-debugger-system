package com.example.debugger;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.LaunchingConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DebuggerService {
    private final Map<String, DebugSession> sessions = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate template;

    @Autowired
    public DebuggerService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public DebugSession createSession() throws Exception {
        String id = UUID.randomUUID().toString();
        VirtualMachine vm = launchVirtualMachine();
        DebugSession session = new DebugSession(id, vm, template);
        sessions.put(id, session);
        return session;
    }

    public void launch(String sessionId, String mainClass) throws Exception {
        DebugSession s = sessions.get(sessionId);
        if (s != null) {
            s.launch(mainClass);
        }
    }

    public void addBreakpoint(String sessionId, String className, int line) throws Exception {
        DebugSession s = sessions.get(sessionId);
        if (s != null) {
            s.addBreakpoint(className, line);
        }
    }

    private VirtualMachine launchVirtualMachine() throws Exception {
        LaunchingConnector connector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Argument> args = connector.defaultArguments();
        // Note: main class is supplied when launching via DebugSession.launch.
        return connector.launch(args);
    }
}
