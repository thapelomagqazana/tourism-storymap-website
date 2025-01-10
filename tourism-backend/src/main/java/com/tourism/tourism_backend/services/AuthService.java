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
 * Provides methods for user registration.
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
     * @return the registered User entity
     * @throws IllegalArgumentException if the email is already in use
     */
    @Transactional
    public AppUser registerUser(UserDTO userDTO) {
        // Check if the email is already in use
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        // Create a new User entity and populate fields
        AppUser user = new AppUser();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        // Hash the password before storing it
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Save the user entity to the database and return the saved entity
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
        // Validate input fields (no trimming of password)
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
    
        // Find user by email (trim email for comparison)
        AppUser user = userRepository.findByEmail(loginRequest.getEmail().trim())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
    
        // Validate password (no trimming or alteration of input password)
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    
        // Generate and return JWT token
        return jwtUtil.generateToken(user.getEmail());
    }

    /**
     * Retrieves the profile of a user by their email.
     *
     * @param email the email of the logged-in user.
     * @return a UserDTO containing the user's profile data.
     * @throws IllegalArgumentException if the user is not found.
     */
    public UserDTO getUserProfile(String email) {
        // Find user by email
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Map the User entity to UserDTO (excluding sensitive fields)
        return new UserDTO(user.getName(), user.getEmail());
    }

    /**
     * Updates the profile of a logged-in user.
     *
     * @param email              the email of the logged-in user
     * @param profileUpdateRequest the profile update request containing new details
     * @return the updated UserDTO
     * @throws UserNotFoundException if the user is not found
     */
    @Transactional
    public UserDTO updateUserProfile(String email, ProfileUpdateRequest profileUpdateRequest) {
        // Find the user by email
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (!user.getEmail().equals(profileUpdateRequest.getEmail()) && userRepository.findByEmail(profileUpdateRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        if ((profileUpdateRequest.getName() == null || profileUpdateRequest.getName().trim().isEmpty()) &&
            (profileUpdateRequest.getEmail() == null || profileUpdateRequest.getEmail().trim().isEmpty()) &&
            (profileUpdateRequest.getPassword() == null || profileUpdateRequest.getPassword().trim().isEmpty())) {
            throw new IllegalArgumentException("At least one field is required");
        }

        if (profileUpdateRequest.getEmail() != null && !profileUpdateRequest.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (profileUpdateRequest.getPassword() != null && profileUpdateRequest.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }        

        // Update fields only if they are provided
        if (profileUpdateRequest.getName() != null && !profileUpdateRequest.getName().trim().isEmpty()) {
            user.setName(profileUpdateRequest.getName().trim());
        }

        if (profileUpdateRequest.getEmail() != null && !profileUpdateRequest.getEmail().trim().isEmpty()) {
            user.setEmail(profileUpdateRequest.getEmail().trim());
        }

        if (profileUpdateRequest.getPassword() != null && !profileUpdateRequest.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(profileUpdateRequest.getPassword()));
        }

        // Save the updated user
        userRepository.save(user);

        // Return the updated profile as a DTO
        return new UserDTO(user.getName(), user.getEmail());
    }
}