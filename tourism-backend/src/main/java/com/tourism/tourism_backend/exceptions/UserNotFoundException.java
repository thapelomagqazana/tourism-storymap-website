package com.tourism.tourism_backend.exceptions;

/**
 * Custom exception thrown when a user is not found.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
