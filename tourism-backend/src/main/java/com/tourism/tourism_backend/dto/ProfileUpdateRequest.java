package com.tourism.tourism_backend.dto;

/**
 * DTO for updating the user profile.
 */
public class ProfileUpdateRequest {

    private String name;
    private String email;
    private String password; // Optional

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
