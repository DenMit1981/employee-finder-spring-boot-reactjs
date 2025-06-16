package com.empire.employeefinder.exception;

public class JobPositionNotFoundException extends RuntimeException {

    public JobPositionNotFoundException(String msg) {
        super(msg);
    }
}
