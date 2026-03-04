package com.foodsystem.user_identity_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // This tells Spring to apply this to all Controllers
public class GlobalExceptionHandler {

    // 1. Handle Duplicate Username/Email (SQL Error 409)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Registration Conflict: That username or email is already taken.");
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // 2. Handle Validation Errors (Missing Fields 400)
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidation(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Missing Information: Please ensure all fields are filled correctly.");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 3. Catch-all for everything else (Generic 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        Map<String, String> body = new HashMap<>();
        body.put("message", "Server Error: " + ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}