package com.teya.ledger.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles invalid enum text passing into movements JSON requests (e.g., "type": "BURRITO")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidJsonInput(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid input. Please ensure the request body is correctly formatted and all required fields are provided with valid values."
        ));
    }

    // Handles invalid amounts or arguments from the business logic layer
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", ex.getMessage()
        ));
    }

    // Handles business state constraints (e.g., Overdraft / Insufficient Balance)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(422).body(Map.of(
                "error", ex.getMessage()
        ));
    }

    // Ultimate catch-all safety net for any uncaught system bugs
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        return ResponseEntity.internalServerError().body(Map.of(
                "error", "An unexpected internal server error occurred."
        ));
    }
}
