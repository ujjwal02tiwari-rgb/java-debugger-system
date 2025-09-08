package com.example.debugger;

public class BreakpointRequest {
    private String className;
    private int line;

    public BreakpointRequest() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
