package com.example.inventory.common;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> notFound(ResourceNotFoundException exception) {
        return error(HttpStatus.NOT_FOUND, List.of(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> invalidBody(MethodArgumentNotValidException exception) {
        List<String> messages = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .toList();
        return error(HttpStatus.BAD_REQUEST, messages);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiError> invalidConstraint(ConstraintViolationException exception) {
        return error(HttpStatus.BAD_REQUEST, List.of(exception.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> dataIntegrity(DataIntegrityViolationException exception) {
        return error(HttpStatus.CONFLICT, List.of("Request conflicts with existing data"));
    }

    private ResponseEntity<ApiError> error(HttpStatus status, List<String> messages) {
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), messages));
    }
}
