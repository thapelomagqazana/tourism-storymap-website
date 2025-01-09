package com.tourism.tourism_backend.services;

import com.tourism.tourism_backend.dto.UserDTO;
import com.tourism.tourism_backend.exceptions.EmailAlreadyExistsException;
import com.tourism.tourism_backend.models.User;
import com.tourism.tourism_backend.repositories.UserRepository;
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
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Registers a new user.
     * 
     * @param userDTO the user data transfer object containing registration details
     * @return the registered User entity
     * @throws IllegalArgumentException if the email is already in use
     */
    @Transactional
    public User registerUser(UserDTO userDTO) {
        // Check if the email is already in use
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }

        // Create a new User entity and populate fields
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        // Hash the password before storing it
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Save the user entity to the database and return the saved entity
        return userRepository.save(user);
    }
}