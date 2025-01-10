package com.tourism.tourism_backend.dto;

/**
 * DTO for returning user profile data.
 */
public class UserProfileResponse {

    private String name;
    private String email;

    // Constructor
    public UserProfileResponse(String name, String email) {
        this.name = name;
        this.email = email;
    }

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
}
