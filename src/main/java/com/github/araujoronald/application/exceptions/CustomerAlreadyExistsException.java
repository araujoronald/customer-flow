package com.github.araujoronald.application.exceptions;

public class CustomerAlreadyExistsException extends BusinessException {
    public CustomerAlreadyExistsException(String message, Object... args) {
        super(message, args);
    }
}