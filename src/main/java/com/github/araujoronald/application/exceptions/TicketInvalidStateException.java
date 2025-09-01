package com.github.araujoronald.application.exceptions;

public class TicketInvalidStateException extends BusinessException {
    public TicketInvalidStateException(String message, Object... args) {
        super(message, args);
    }
}