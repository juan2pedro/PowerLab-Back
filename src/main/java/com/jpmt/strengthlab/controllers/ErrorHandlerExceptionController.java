package com.jpmt.strengthlab.controllers;

import com.jpmt.strengthlab.exceptions.ResourceNotFoundException;
import com.jpmt.strengthlab.models.dto.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandlerExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlerExceptionController.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiErrorResponse apiError = new ApiErrorResponse(
                errors,
                "Validation failed for one or more fields",
                "Validation Error",
                HttpStatus.BAD_REQUEST.value(),
                new Date()
        );

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiErrorResponse apiError = new ApiErrorResponse(
                null,
                ex.getMessage(),
                "Resource Not Found",
                HttpStatus.NOT_FOUND.value(),
                new Date()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        ApiErrorResponse apiError = new ApiErrorResponse(
                null,
                ex.getMessage(),
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        logger.warn("Data integrity violation", ex);
        ApiErrorResponse apiError = new ApiErrorResponse(
                null, "Conflict with existing data", "Data Integrity Violation", HttpStatus.CONFLICT.value(), new Date()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        ApiErrorResponse apiError = new ApiErrorResponse(
                null, ex.getMessage(), "Constraint Violation", HttpStatus.BAD_REQUEST.value(), new Date()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

}
