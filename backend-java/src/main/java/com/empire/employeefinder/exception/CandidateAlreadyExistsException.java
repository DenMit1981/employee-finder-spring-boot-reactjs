package com.empire.employeefinder.exception;

public class CandidateAlreadyExistsException extends RuntimeException {

    public CandidateAlreadyExistsException() {
        super("This candidate has already been added to list");
    }
}
