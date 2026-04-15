package com.project.edugov.exception;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.project.edugov.dto.ErrorResponse;
// Make sure ErrorDetails is imported here!
// import com.project.edugov.dto.ErrorDetails; 
import com.project.edugov.exception.EduGovExceptions.AccountNotActiveException;
import com.project.edugov.exception.EduGovExceptions.InvalidCredentialsException;
import com.project.edugov.exception.EduGovExceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Helper method to clean up the URL path for the JSON response
    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    // ==========================================
    // --- AUTHENTICATION EXCEPTIONS (401, 403) ---
    // ==========================================

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        logger.error("401 Unauthorized: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountNotActiveException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotActive(AccountNotActiveException ex) {
        logger.error("403 Forbidden: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Forbidden", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(AccessDeniedException ex) {
        logger.warn("403 - AccessDenied: {}", ex.getMessage());
        return new ResponseEntity<>(buildError(403, "Forbidden", ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    // ==========================================
    // --- NOT FOUND EXCEPTIONS (404) -----------
    // ==========================================

    // Merged both ResourceNotFound and EntityNotFound into one handler
    @ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorDetails> handleResourceNotFound(RuntimeException ex, WebRequest request) {
        logger.error("404 Not Found: {}", ex.getMessage()); 
        ErrorDetails error = new ErrorDetails(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), getPath(request));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // ==========================================
    // --- BAD REQUESTS (400) -------------------
    // ==========================================

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorDetails> handleAPIException(APIException ex, WebRequest request) {
        HttpStatus status = (ex.getStatus() != null) ? ex.getStatus() : HttpStatus.BAD_REQUEST;
        ErrorDetails error = new ErrorDetails(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                ex.getMessage(), getPath(request));
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(EnrollmentException.class)
    public ResponseEntity<ErrorDetails> handleEnrollmentError(EnrollmentException ex, WebRequest request) {
        ErrorDetails error = new ErrorDetails(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage(), getPath(request));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Merged IllegalArgumentException & IllegalStateException
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorDetails> handleBadRequest(RuntimeException ex, WebRequest request) {
        logger.error("400 - Bad Request: {}", ex.getMessage());
        ErrorDetails error = new ErrorDetails(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage(), getPath(request));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // ==========================================
    // --- VALIDATION ERRORS (422) --------------
    // ==========================================

    // Kept the advanced Module 5 logic that returns all field errors in a Map
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        logger.error("422 - Validation Error in Request Body");

        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> details.put(e.getField(), e.getDefaultMessage()));

        Map<String, Object> body = buildError(422, "Validation Failed", "Invalid inputs");
        body.put("details", details);
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException ex) {
        logger.error("422 - ConstraintViolation: {}", ex.getMessage());

        Map<String, String> details = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(v -> details.put(String.valueOf(v.getPropertyPath()), v.getMessage()));

        Map<String, Object> body = buildError(422, "Constraint Violation", "Invalid inputs");
        body.put("details", details);
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // ==========================================
    // --- CONFLICTS (409) ----------------------
    // ==========================================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataConflict(DataIntegrityViolationException ex) {
        logger.error("409 - Data Integrity Violation: {}", ex.getMessage());
        return new ResponseEntity<>(buildError(409, "Data Conflict", "Action cannot be completed due to database constraints"), HttpStatus.CONFLICT);
    }

    // ==========================================
    // --- GLOBAL CATCH-ALL (500) ---------------
    // ==========================================

    // Merged Catch-All Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("500 Internal Server Error: ", ex);
        ErrorDetails error = new ErrorDetails(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal Server Error: " + ex.getMessage(),
                getPath(request));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ==========================================
    // --- HELPER METHODS -----------------------
    // ==========================================

    private Map<String, Object> buildError(int status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        return body;
    }
    
//   @ExceptionHandler(MethodArgumentNotValidException.class)
//public ResponseEntity<ErrorDetails> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
//		// Extract the "default message" you wrote in your @AssertTrue
//		String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
//           ErrorDetails errorDetails = new ErrorDetails(
//		LocalDateTime.now(),
//		errorMessage,
//		request.getDescription(false)
//	);
//     return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
//}
    
}