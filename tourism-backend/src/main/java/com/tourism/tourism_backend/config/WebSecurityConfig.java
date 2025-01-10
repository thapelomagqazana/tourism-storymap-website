package com.tourism.tourism_backend.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.tourism.tourism_backend.filters.JwtFilter;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.beans.factory.annotation.Value;

/**
 * WebSecurityConfig class defines the security configuration for the application.
 * This configuration ensures proper access control and disables CSRF protection for simplicity during testing.
 */
@Configuration
public class WebSecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Configures the security filter chain for the application.
     * 
     * @param http an instance of HttpSecurity used to configure security settings.
     * @return a SecurityFilterChain that defines the application's security behavior.
     * @throws Exception if any error occurs while configuring HttpSecurity.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtAuthenticationFilter) throws Exception {
        // Disable Cross-Site Request Forgery (CSRF) protection for simplicity in development and testing environments.
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permit all requests to the user registration endpoint without authentication.
                .requestMatchers("/api/users/register", "/api/users/login").permitAll()
                .requestMatchers( "/api/users/logout", "/api/users/profile").authenticated()
                // Require authentication for all other requests.
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())); // Ensure JWT is used for authentication;

        // Build and return the configured SecurityFilterChain.
        return http.build();
    }

    /**
     * Creates a JwtDecoder bean using the secret key from application.properties.
     *
     * @return JwtDecoder for validating JWT tokens.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        // Convert the secret key string to a SecretKey instance
        byte[] secretBytes = jwtSecret.getBytes();
        SecretKey secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

}
