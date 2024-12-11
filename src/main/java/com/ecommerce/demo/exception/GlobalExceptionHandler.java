package com.ecommerce.demo.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    // Handle validation exceptions (e.g., request body validation)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {

        log.error("MethodArgumentNotValidException occurred: {}", ex.getMessage(), ex);
        String message = "Validation failed: " + Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    // Handle method not supported exceptions
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest request) {

        log.error("HttpRequestMethodNotSupportedException occurred: {}", ex.getMessage(), ex);
        String message = "Request method " + ex.getMethod() + " not supported.";

        return new ResponseEntity<>(message, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Handle entity not found exceptions (e.g., when looking for a record in the database)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {

        log.error("EntityNotFoundException occurred: {}", ex.getMessage(), ex);
        String message = ex.getMessage();

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    // Handle data integrity violations (e.g., unique constraint violations)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {

        log.error("DataIntegrityViolationException occurred: {}", ex.getMessage(), ex);

        String message = "Data integrity violation: " + ex.getMostSpecificCause().getMessage();

        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    // Handle constraint violations (e.g., when an entity fails validation)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {

        log.error("ConstraintViolationException occurred: {}", ex.getMessage(), ex);

        String message = "Constraint violation: " + ex.getMessage();

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    // Handle access denied exceptions (e.g., security-related exceptions)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {

        log.error("AccessDeniedException occurred: {}", ex.getMessage(), ex);

        String message = "Access denied: " + ex.getMessage();

        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    // Handle all other exceptions that don't have specific handlers
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {

        log.error("Exception occurred: {}", ex.getMessage(), ex);

        String message = "An unexpected error occurred: " + ex.getMessage();

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
