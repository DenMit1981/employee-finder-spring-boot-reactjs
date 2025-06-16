package com.empire.employeefinder.exception;

public class ForbiddenSuperadminDeleteException extends RuntimeException {

    public ForbiddenSuperadminDeleteException() {
        super("You cannot delete the role of a SUPERADMIN user");
    }
}
