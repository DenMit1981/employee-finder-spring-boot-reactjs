package com.empire.employeefinder.exception;

public class FileSizeExceededException extends RuntimeException {

    private static final String ALLOWED_MAXIMUM_SIZE_ERROR_MESSAGE = "The size of the attached file should not be greater than 5 Mb. " +
            "Please select another file.";

    public FileSizeExceededException() {
        super(ALLOWED_MAXIMUM_SIZE_ERROR_MESSAGE);
    }
}
