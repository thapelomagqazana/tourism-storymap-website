package com.tourism.tourism_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration and profile data.
 */
public class UserDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    // Default constructor
    public UserDTO() {
    }

    // Constructor for returning profile data (excluding password)
    public UserDTO(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Constructor with parameters (name, email, password, role)
    public UserDTO(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Constructor for user registration
    public UserDTO(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "USER";
    }

    // Getters and Setters
    public String getName() {
        return name != null ? name.trim() : null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email != null ? email.trim() : null;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password != null ? password.trim() : null;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role != null ? role.trim() : null;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
