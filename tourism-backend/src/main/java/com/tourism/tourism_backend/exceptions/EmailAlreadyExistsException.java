package com.tourism.tourism_backend.exceptions;

/**
 * Custom exception for handling duplicate email registration errors.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
