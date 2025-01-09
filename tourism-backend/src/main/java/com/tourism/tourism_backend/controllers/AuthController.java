package com.tourism.tourism_backend.controllers;

import com.tourism.tourism_backend.dto.LoginRequest;
import com.tourism.tourism_backend.dto.UserDTO;
import com.tourism.tourism_backend.models.User;
import com.tourism.tourism_backend.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling user authentication-related endpoints.
 */
@RestController
@RequestMapping("/api/users")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint for registering a new user.
     * 
     * @param userDTO the user data transfer object containing registration details
     * @return a ResponseEntity containing the registered user
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserDTO userDTO) {
        // Call the service method to register the user
        User registeredUser = authService.registerUser(userDTO);

        // Return the registered user in the response
        return ResponseEntity.ok(registeredUser);
    }

    /**
     * Logs in a user and returns a JWT token on successful authentication.
     *
     * @param loginRequest the login request containing email and password
     * @return a ResponseEntity with a JWT token or an error message
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok("{\"token\": \"" + token + "\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"error\": \"Invalid email or password\"}");
        }
    }
}


