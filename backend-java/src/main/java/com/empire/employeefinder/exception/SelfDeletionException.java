package com.empire.employeefinder.exception;

public class SelfDeletionException extends RuntimeException {

    public SelfDeletionException() {
        super("User cannot delete themselves");
    }
}
