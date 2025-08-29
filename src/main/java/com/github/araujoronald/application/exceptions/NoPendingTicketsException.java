package com.github.araujoronald.application.exceptions;

public class NoPendingTicketsException extends RuntimeException {
    public NoPendingTicketsException(String message) {
        super(message);
    }
}