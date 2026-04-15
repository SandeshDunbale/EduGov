package com.project.edugov.exception;

public class EduGovExceptions {

    // Thrown when a user, email, or record isn't found (404)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    // Thrown when passwords don't match (401)
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }

    // Thrown when a user tries to log in but their status is PENDING or DECLINED (403)
    public static class AccountNotActiveException extends RuntimeException {
        public AccountNotActiveException(String message) {
            super(message);
        }
    }
}