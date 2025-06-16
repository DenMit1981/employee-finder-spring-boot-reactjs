package com.empire.employeefinder.exception;

public class ResumeNotFoundException extends RuntimeException {

    public ResumeNotFoundException() {
        super("Resume not found");
    }
}
