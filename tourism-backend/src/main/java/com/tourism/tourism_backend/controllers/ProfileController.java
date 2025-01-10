package com.tourism.tourism_backend.controllers;

import com.tourism.tourism_backend.dto.ProfileUpdateRequest;
import com.tourism.tourism_backend.dto.UserDTO;
import com.tourism.tourism_backend.exceptions.EmailAlreadyExistsException;
import com.tourism.tourism_backend.exceptions.UserNotFoundException;
import com.tourism.tourism_backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller for handling user profile operations.
 */
@RestController
@RequestMapping("/api/users")
public class ProfileController {

    @Autowired
    private AuthService authService;

    /**
     * Retrieves the profile of the logged-in user.
     *
     * @param principal the authenticated user details.
     * @return a ResponseEntity containing the user's profile data.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Principal principal) {
        try {
            // Use the principal (authenticated user's email) to get the user profile
            UserDTO userProfile = authService.getUserProfile(principal.getName());
            return ResponseEntity.ok(userProfile);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"User not found\"}");
        }
    }

    /**
     * Updates the profile of the logged-in user.
     *
     * @param profileUpdateRequest the profile update request containing new details
     * @param principal            the authenticated user's details
     * @return a ResponseEntity containing the updated profile or an error message
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody ProfileUpdateRequest profileUpdateRequest,
                                            Principal principal) {
        try {
            // Update user profile using the email from the principal
            UserDTO updatedProfile = authService.updateUserProfile(principal.getName(), profileUpdateRequest);
            return ResponseEntity.ok(updatedProfile);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"User not found\"}");
        } catch (EmailAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Email is already in use\"}");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"" + ex.getMessage() + "\"}");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Failed to update profile\"}");
        }
    }

}
