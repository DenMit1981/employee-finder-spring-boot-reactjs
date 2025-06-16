package com.empire.employeefinder.exception;

public class SelfRoleModificationException extends RuntimeException {

    public SelfRoleModificationException() {
        super("You cannot change your own role");
    }
}
