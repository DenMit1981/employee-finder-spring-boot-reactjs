package com.empire.employeefinder.exception;

public class FileAlreadyExistsException extends RuntimeException {

    public FileAlreadyExistsException() {
        super("File already exists for this resume");
    }
}
