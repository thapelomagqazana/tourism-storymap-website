package com.tourism.tourism_backend.services;

import com.tourism.tourism_backend.dto.LoginRequest;
import com.tourism.tourism_backend.dto.ProfileUpdateRequest;
import com.tourism.tourism_backend.dto.UserDTO;
import com.tourism.tourism_backend.exceptions.EmailAlreadyExistsException;
import com.tourism.tourism_backend.exceptions.InvalidCredentialsException;
import com.tourism.tourism_backend.exceptions.UserNotFoundException;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.repositories.UserRepository;
import com.tourism.tourism_backend.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling authentication-related operations.
 * Provides methods for user registration, authentication, profile retrieval, and updates.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Registers a new user.
     * 
     * @param userDTO the user data transfer object containing registration details
     * @return the registered AppUser entity
     * @throws EmailAlreadyExistsException if the email is already in use
     */
    @Transactional
    public AppUser registerUser(UserDTO userDTO) {
        // Check if the email is already in use
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        // Create a new AppUser entity and set its fields
        AppUser user = new AppUser();
        user.setName(userDTO.getName().trim());
        user.setEmail(userDTO.getEmail().trim());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword().trim()));
        user.setRole(userDTO.getRole().trim().toUpperCase()); // Normalize role to uppercase

        // Save the user entity and return the result
        return userRepository.save(user);
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param loginRequest the login request containing email and password
     * @return the generated JWT token
     * @throws InvalidCredentialsException if authentication fails
     */
    public String authenticateUser(LoginRequest loginRequest) {
        // Validate email and password inputs
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        // Retrieve user by email
        AppUser user = userRepository.findByEmail(loginRequest.getEmail().trim())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Validate the provided password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate and return a JWT token
        return jwtUtil.generateToken(user.getEmail(), user.getRole());
    }

    /**
     * Retrieves the profile of a user by their email.
     *
     * @param email the email of the logged-in user
     * @return a UserDTO containing the user's profile data
     * @throws UserNotFoundException if the user is not found
     */
    public UserDTO getUserProfile(String email) {
        // Find user by email
        AppUser user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Return a DTO with the user's profile data (excluding password)
        return new UserDTO(user.getName(), user.getEmail());
    }

    /**
     * Updates the profile of a logged-in user.
     *
     * @param email                the email of the logged-in user
     * @param profileUpdateRequest the profile update request containing new details
     * @return the updated UserDTO
     * @throws UserNotFoundException if the user is not found
     * @throws EmailAlreadyExistsException if the new email is already in use
     * @throws IllegalArgumentException if the input is invalid
     */
    @Transactional
    public UserDTO updateUserProfile(String email, ProfileUpdateRequest profileUpdateRequest) {
        // Find the user by email
        AppUser user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Check if a new email is provided and is already in use by another user
        if (profileUpdateRequest.getEmail() != null &&
            !user.getEmail().equals(profileUpdateRequest.getEmail().trim()) &&
            userRepository.findByEmail(profileUpdateRequest.getEmail().trim()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        // Check for empty or invalid update requests
        if ((profileUpdateRequest.getName() == null || profileUpdateRequest.getName().trim().isEmpty()) &&
            (profileUpdateRequest.getEmail() == null || profileUpdateRequest.getEmail().trim().isEmpty()) &&
            (profileUpdateRequest.getPassword() == null || profileUpdateRequest.getPassword().trim().isEmpty())) {
            throw new IllegalArgumentException("At least one field is required for update");
        }

        // Validate new email format if provided
        if (profileUpdateRequest.getEmail() != null &&
            !profileUpdateRequest.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Validate new password length if provided
        if (profileUpdateRequest.getPassword() != null && profileUpdateRequest.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        // Update user fields only if new values are provided
        if (profileUpdateRequest.getName() != null && !profileUpdateRequest.getName().trim().isEmpty()) {
            user.setName(profileUpdateRequest.getName().trim());
        }

        if (profileUpdateRequest.getEmail() != null && !profileUpdateRequest.getEmail().trim().isEmpty()) {
            user.setEmail(profileUpdateRequest.getEmail().trim());
        }

        if (profileUpdateRequest.getPassword() != null && !profileUpdateRequest.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(profileUpdateRequest.getPassword().trim()));
        }

        // Save the updated user to the repository
        userRepository.save(user);

        // Return the updated profile as a DTO
        return new UserDTO(user.getName(), user.getEmail());
    }
}
