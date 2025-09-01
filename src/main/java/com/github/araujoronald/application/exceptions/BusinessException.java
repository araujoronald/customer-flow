package com.github.araujoronald.application.exceptions;

public abstract class BusinessException extends RuntimeException {

    private final Object[] args;

    public BusinessException(String message, Object... args) {
        super(message);
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}