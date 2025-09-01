package com.github.araujoronald.application.exceptions;

public class CustomerNotFoundException extends BusinessException {
    public CustomerNotFoundException(String message, Object... args) {
        super(message, args);
    }
}