package com.empire.employeefinder.exception;

public class ResumeFileNotFoundException extends RuntimeException {

    public ResumeFileNotFoundException() {
        super("Resume file is required and was not provided");
    }
}

