package com.github.araujoronald.application.exceptions;

public class AttendantAlreadyExistsException extends BusinessException {
    public AttendantAlreadyExistsException(String message, Object... args) {
        super(message);
    }
}