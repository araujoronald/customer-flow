package com.github.araujoronald.infra.api.rest.springboot.exceptions;

import com.github.araujoronald.application.exceptions.AttendantAlreadyExistsException;
import com.github.araujoronald.application.exceptions.AttendantNotFoundException;
import com.github.araujoronald.application.exceptions.BusinessException;
import com.github.araujoronald.application.exceptions.CustomerAlreadyExistsException;
import com.github.araujoronald.application.exceptions.CustomerNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    private final MessageSource messageSource;

    public RestExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({CustomerNotFoundException.class, AttendantNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(BusinessException ex, HttpServletRequest request, Locale locale) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request, locale);
    }

    @ExceptionHandler({CustomerAlreadyExistsException.class, AttendantAlreadyExistsException.class})
    public ResponseEntity<ErrorResponse> handleConflict(BusinessException ex, HttpServletRequest request, Locale locale) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT, request, locale);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("Field '%s': %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, HttpServletRequest request, Locale locale) {
        // Business logic violations like trying to cancel a completed ticket
        return buildErrorResponse(ex, ex.getMessage(), new Object[]{}, HttpStatus.UNPROCESSABLE_ENTITY, request, locale);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request, Locale locale) {
        // Fallback for any other unexpected exception
        return buildErrorResponse(ex, "An unexpected internal server error has occurred.", new Object[]{}, HttpStatus.INTERNAL_SERVER_ERROR, request, locale);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(BusinessException ex, HttpStatus status, HttpServletRequest request, Locale locale) {
        return buildErrorResponse(ex, ex.getMessage(), ex.getArgs(), status, request, locale);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, String messageKey, Object[] args, HttpStatus status, HttpServletRequest request, Locale locale) {
        String message = messageSource.getMessage(messageKey, args, messageKey, locale);
        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(errorResponse);
    }
}