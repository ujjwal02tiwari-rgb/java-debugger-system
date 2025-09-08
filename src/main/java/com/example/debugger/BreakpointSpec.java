package com.example.debugger;

public class BreakpointSpec {
    public final String className;
    public final int line;
    
    private BreakpointSpec(String className, int line) {
        this.className = className;
        this.line = line;
    }
    
    public static BreakpointSpec line(String className, int line) {
        return new BreakpointSpec(className, line);
    }
    
    @Override
    public String toString() {
        return className + ":" + line;
    }
}
