package com.empire.employeefinder.exception;

public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException(String msg) {
        super(msg);
    }
}
