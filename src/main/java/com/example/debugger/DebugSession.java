package com.example.debugger;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDisconnectEvent;

import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

import java.util.List;
import java.util.Map;


public class DebugSession {
    private final String id;
    private VirtualMachine vm;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final SimpMessagingTemplate template;

    public DebugSession(String id, VirtualMachine vm, SimpMessagingTemplate template) {
        this.id = id;
        this.vm = vm;
        this.template = template;
        startEventLoop();
    }

    public String getId() {
        return id;
    }

    /**
     * Launch a new JVM with the given main class.  You can extend this
     * to accept a classpath and VM arguments via a LaunchRequest DTO.
     */
    public void launch(String mainClass) throws Exception {
        if (vm != null) {
            throw new IllegalStateException("VM already initialised");
        }
        // Use the default LaunchingConnector
        LaunchingConnector connector =
            Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> args = connector.defaultArguments();
        // Set the class to launch (and optionally options or suspend=y)
        args.get("main").setValue(mainClass);
        // Launch the new VM
        vm = connector.launch(args);
        startEventLoop();
    }

    /**
     * Alternatively, attach to an already-running process.
     * For example, use the SocketAttachingConnector on host:port.
     */
    public void attach(String hostname, int port) throws Exception {
        if (vm != null) {
            vm.dispose();
        }
        VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
        AttachingConnector connector = vmm.attachingConnectors().stream()
            .filter(c -> c.name().equals("com.sun.jdi.SocketAttach"))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("SocketAttach connector not available"));
        Map<String, Connector.Argument> args = connector.defaultArguments();
        args.get("hostname").setValue(hostname);
        args.get("port").setValue(String.valueOf(port));
        vm = connector.attach(args);
        startEventLoop();
    }

    public void addBreakpoint(String className, int line) throws Exception {
        ReferenceType ref = findReferenceType(className);
        if (ref == null) throw new IllegalArgumentException("Class not loaded: " + className);
        Location loc = ref.locationsOfLine(line).get(0);
        EventRequestManager mgr = vm.eventRequestManager();
        BreakpointRequest req = mgr.createBreakpointRequest(loc);
        req.enable();
    }

    /** Step into the next line for a given thread. */
    public void stepInto(String threadName) throws Exception {
        ThreadReference thread = findThread(threadName);
        EventRequestManager mgr = vm.eventRequestManager();
        StepRequest step = mgr.createStepRequest(thread,
            StepRequest.STEP_LINE, StepRequest.STEP_INTO);
        step.addCountFilter(1); // Trigger once
        step.enable();
        vm.resume();
    }

    /** Step over the next line. */
    public void stepOver(String threadName) throws Exception {
        ThreadReference thread = findThread(threadName);
        EventRequestManager mgr = vm.eventRequestManager();
        StepRequest step = mgr.createStepRequest(thread,
            StepRequest.STEP_LINE, StepRequest.STEP_OVER);
        step.addCountFilter(1);
        step.enable();
        vm.resume();
    }

    /** Inspect local variables in the top stack frame of the given thread. */
    public Map<String, String> inspectVariables(String threadName) throws Exception {
        ThreadReference thread = findThread(threadName);
        StackFrame frame = thread.frame(0);
        Map<LocalVariable, Value> values = frame.getValues(frame.visibleVariables());
        return values.entrySet().stream()
            .collect(Collectors.toMap(
                e -> e.getKey().name(),
                e -> String.valueOf(e.getValue())
            ));
    }

    private ReferenceType findReferenceType(String className) {
        return vm.classesByName(className).stream().findFirst().orElse(null);
    }

    private ThreadReference findThread(String name) {
        return vm.allThreads().stream()
            .filter(t -> t.name().equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Thread not found: " + name));
    }

    private void startEventLoop() {
        executor.submit(() -> {
            EventQueue queue = vm.eventQueue();
            try {
                while (true) {
                    EventSet events = queue.remove();
                    for (Event event : events) {
                        if (event instanceof BreakpointEvent) {
                            handleBreakpoint((BreakpointEvent) event);
                        } else if (event instanceof StepEvent) {
                            handleStep((StepEvent) event);
                        } else if (event instanceof VMDisconnectEvent) {
                            // VM exited; clean up and exit loop
                            return;
                        }
                        // handle more events (e.g. exceptions, thread death) here
                    }
                    events.resume();
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void handleBreakpoint(BreakpointEvent bp) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "breakpointHit");
        payload.put("location", bp.location().toString());
        payload.put("threadName", bp.thread().name());
        template.convertAndSend("/topic/debug/" + id, payload);
    }

    private void handleStep(StepEvent se) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "stepComplete");
        payload.put("location", se.location().toString());
        payload.put("threadName", se.thread().name());
        template.convertAndSend("/topic/debug/" + id, payload);
    }

    public void close() {
        if (vm != null) {
            vm.dispose();
        }
        executor.shutdownNow();
    }
}
