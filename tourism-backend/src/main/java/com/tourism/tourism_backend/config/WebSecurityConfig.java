package com.tourism.tourism_backend.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.beans.factory.annotation.Value;

import com.tourism.tourism_backend.filters.JwtFilter;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Collection;

/**
 * WebSecurityConfig class defines the security configuration for the application.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/register", "/api/users/login", "/api/attractions").permitAll()
                .requestMatchers("/api/users/logout", "/api/users/profile",
                                "/api/attractions/{id}/traffic", "/api/trips").authenticated()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required"))
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> 
                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
            ));

        return http.build();
    }

    /**
     * Creates a JwtDecoder bean using the secret key from application.properties.
     *
     * @return JwtDecoder for validating JWT tokens.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretBytes = jwtSecret.getBytes();
        SecretKey secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }

    /**
     * Custom JwtAuthenticationConverter to correctly map roles without prefixing "ROLE_".
     *
     * @return a Converter that converts Jwt tokens to authentication tokens.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
    
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
