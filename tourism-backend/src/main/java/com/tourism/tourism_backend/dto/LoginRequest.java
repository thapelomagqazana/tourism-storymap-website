package com.tourism.tourism_backend.dto;

/**
 * DTO representing a login request.
 * Contains fields for email and password.
 */
public class LoginRequest {

    private String email;
    private String password;

    // Default constructor
    public LoginRequest() {
    }

    // Constructor with parameters (email, password)
    public LoginRequest(String email, String password) {
        this.email = email != null ? email.trim() : null;
        this.password = password != null ? password.trim() : null;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password != null ? password.trim() : null;
    }
}
