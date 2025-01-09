package com.tourism.tourism_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * WebSecurityConfig class defines the security configuration for the application.
 * This configuration ensures proper access control and disables CSRF protection for simplicity during testing.
 */
@Configuration
public class WebSecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * 
     * @param http an instance of HttpSecurity used to configure security settings.
     * @return a SecurityFilterChain that defines the application's security behavior.
     * @throws Exception if any error occurs while configuring HttpSecurity.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable Cross-Site Request Forgery (CSRF) protection for simplicity in development and testing environments.
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permit all requests to the user registration endpoint without authentication.
                .requestMatchers("/api/users/register", "/api/users/login", "/api/users/logout").permitAll()
                // Require authentication for all other requests.
                .anyRequest().authenticated()
            );

        // Build and return the configured SecurityFilterChain.
        return http.build();
    }
}
