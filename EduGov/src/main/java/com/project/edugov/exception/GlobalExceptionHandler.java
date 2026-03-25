package com.project.edugov.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ----------------------------- 404 NOT FOUND -----------------------------
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(EntityNotFoundException ex) {
        log.error("404 - EntityNotFoundException: {}", ex.getMessage());
        return buildError(404, "Not Found", ex.getMessage());
    }

    // ----------------------------- 400 BAD REQUEST -----------------------------
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(RuntimeException ex) {
        log.error("400 - BadRequest: {}", ex.getMessage());
        return buildError(400, "Bad Request", ex.getMessage());
    }

    // ----------------------------- 403 FORBIDDEN -----------------------------
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleForbidden(AccessDeniedException ex) {
        log.warn("403 - AccessDenied: {}", ex.getMessage());
        return buildError(403, "Forbidden", ex.getMessage());
    }

    // ----------------------------- 409 CONFLICT -----------------------------
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleDataConflict(DataIntegrityViolationException ex) {
        log.error("409 - Data Integrity Violation: {}", ex.getMessage());
        return buildError(409, "Data Conflict", "Action cannot be completed due to database constraints");
    }

    // ----------------------------- 422 VALIDATION (BODY) -----------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        log.error("422 - Validation Error in Request Body");

        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> details.put(e.getField(), e.getDefaultMessage()));

        Map<String, Object> body = buildError(422, "Validation Failed", "Invalid inputs");
        body.put("details", details);
        return body;
    }

    // ----------------------------- 422 VALIDATION (QUERY PARAMS) -----------------------------
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, Object> handleConstraint(ConstraintViolationException ex) {
        log.error("422 - ConstraintViolation: {}", ex.getMessage());

        Map<String, String> details = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(v -> details.put(String.valueOf(v.getPropertyPath()), v.getMessage()));

        Map<String, Object> body = buildError(422, "Constraint Violation", "Invalid inputs");
        body.put("details", details);
        return body;
    }

    // ----------------------------- 500 INTERNAL SERVER ERROR -----------------------------
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleOther(Exception ex) {
        log.error("500 - Unexpected Error: {}", ex.getMessage(), ex);
        return buildError(500, "Internal Server Error", ex.getMessage());
    }

    // ----------------------------- COMMON ERROR BODY BUILDER -----------------------------
    private Map<String, Object> buildError(int status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }
}