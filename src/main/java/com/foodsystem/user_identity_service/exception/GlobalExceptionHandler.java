package com.foodsystem.user_identity_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

import java.net.ResponseCache;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Duplicate Username/Email
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(org.springframework.dao.DataIntegrityViolationException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Registration Conflict: That username or email is already taken.");
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // 2. Handle standard Validation Errors (Better for Swagger scanning)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Validation Failed: Please ensure all fields are filled correctly.");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 3. Handle Inter-Service Communication Failures
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, String>> handleServiceUnavailable(ResourceAccessException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message","Integration Error: Unable to communicate with dependent services. Please try again later.");
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }
    // 4. Catch-all for everything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Server Error: " + ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}