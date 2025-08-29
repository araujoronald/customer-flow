package com.github.araujoronald.application.exceptions;

public class AttendantNotFoundException extends RuntimeException {
    public AttendantNotFoundException(String message) {
        super(message);
    }
}