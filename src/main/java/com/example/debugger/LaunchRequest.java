package com.example.debugger;

public class LaunchRequest {
    private String mainClass;
    private String args;

    public LaunchRequest() {}

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}

