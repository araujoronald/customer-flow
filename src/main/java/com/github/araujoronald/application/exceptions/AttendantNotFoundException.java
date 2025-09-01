package com.github.araujoronald.application.exceptions;

public class AttendantNotFoundException extends BusinessException {
    public AttendantNotFoundException(String message, Object... args) {
        super(message);
    }
}