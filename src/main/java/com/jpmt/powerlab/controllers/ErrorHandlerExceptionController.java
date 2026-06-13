package com.jpmt.powerlab.controllers;

import com.jpmt.powerlab.exceptions.BadRequestException;
import com.jpmt.powerlab.exceptions.ResourceNotFoundException;
import com.jpmt.powerlab.models.dto.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@SuppressWarnings("unused")
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

        logger.warn("Validation failed: {}", errors);

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
        logger.debug("Resource not found: {}", ex.getMessage());

        ApiErrorResponse apiError = new ApiErrorResponse(
                null,
                "Resource not found",
                "Resource Not Found",
                HttpStatus.NOT_FOUND.value(),
                new Date()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(BadRequestException ex) {
        logger.warn("Bad request: {}", ex.getMessage());
        ApiErrorResponse apiError = new ApiErrorResponse(
                null,
                ex.getMessage(),
                "Bad Request",
                HttpStatus.BAD_REQUEST.value(),
                new Date()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatusCode statusCode = errorResponse.getStatusCode();
            HttpStatus status = HttpStatus.resolve(statusCode.value());
            logger.warn("Request error ({}): {}", statusCode.value(), ex.getMessage());
            ApiErrorResponse apiError = new ApiErrorResponse(
                    null,
                    ex.getMessage(),
                    status != null ? status.getReasonPhrase() : "Request Error",
                    statusCode.value(),
                    new Date()
            );
            return ResponseEntity.status(statusCode).body(apiError);
        }

        logger.error("Unhandled exception", ex);
        ApiErrorResponse apiError = new ApiErrorResponse(
                null,
                "Se produjo un error procesando la solicitud",
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
        logger.warn("Constraint violation", ex);
        ApiErrorResponse apiError = new ApiErrorResponse(
                null, "Validación de restricción fallida", "Constraint Violation", HttpStatus.BAD_REQUEST.value(), new Date()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "valor válido";
        String message = String.format("El parámetro '%s' tiene un valor no válido: '%s'. Se esperaba un %s.",
                ex.getName(), ex.getValue(), requiredType);
        logger.warn("Type mismatch: {}", message);
        ApiErrorResponse apiError = new ApiErrorResponse(
                Map.of(ex.getName(), "valor no válido: '" + ex.getValue() + "'"),
                message, "Type Mismatch", HttpStatus.BAD_REQUEST.value(), new Date()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("Http message not readable", ex);
        ApiErrorResponse apiError = new ApiErrorResponse(
                null, "Mensaje HTTP no legible", "Http Message Not Readable", HttpStatus.BAD_REQUEST.value(), new Date()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
}
