package com.github.araujoronald.infra.api.rest.springboot.exceptions;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path
) {
}