package com.example.debugger.dto;

public class BreakpointRequest {
    private String className;
    private Integer line;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }
}
