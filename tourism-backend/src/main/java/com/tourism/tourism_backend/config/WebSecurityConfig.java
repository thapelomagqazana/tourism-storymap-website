package com.tourism.tourism_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity in tests
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/register").permitAll() // Allow public access to register endpoint
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
