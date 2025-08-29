package com.github.araujoronald.application.exceptions;

public class AttendantAlreadyExistsException extends RuntimeException {
    public AttendantAlreadyExistsException(String message) {
        super(message);
    }
}