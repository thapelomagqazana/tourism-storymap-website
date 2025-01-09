package com.tourism.tourism_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration class for security-related beans.
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean definition for BCryptPasswordEncoder.
     * Used for hashing passwords before storing them in the database.
     * 
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


