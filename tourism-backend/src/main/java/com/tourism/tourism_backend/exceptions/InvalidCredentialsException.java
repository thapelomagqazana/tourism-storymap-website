package com.tourism.tourism_backend.exceptions;

/**
 * Custom exception for handling invalid credentials during login.
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
