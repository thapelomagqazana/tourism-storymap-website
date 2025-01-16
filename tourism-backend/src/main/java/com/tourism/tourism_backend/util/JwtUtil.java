package com.tourism.tourism_backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

/**
 * Utility class for handling JWT operations.
 */
@Component
public class JwtUtil {

    private String jwtSecret;
    private long jwtExpirationMs;
    private Key signingKey;

    /**
     * Initializes the JWT utility by loading configuration from environment variables
     * and preparing the signing key.
     */
    @PostConstruct
    public void init() {
        // Load environment variables using Dotenv
        Dotenv dotenv = Dotenv.configure().load();

        jwtSecret = dotenv.get("JWT_SECRET");
        jwtExpirationMs = Long.parseLong(dotenv.get("JWT_EXPIRATION_MS"));

        // Create signing key using HMAC and the secret key
        signingKey = new SecretKeySpec(jwtSecret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * Generates a JWT token with the default expiration time.
     *
     * @param email the subject (email) for the token
     * @param role  the subject (role) for the token
     * @return the generated token
     */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("roles", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a JWT token with a short expiration time for testing purposes.
     *
     * @param email the subject (email) for the token
     * @return the generated token
     */
    public String generateTokenWithShortExpiry(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60)) // 1 minute expiry
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parses the JWT token and extracts claims.
     *
     * @param token the JWT token
     * @return the claims contained in the token
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates a JWT token with an expired expiration time for testing purposes.
     *
     * @param email the subject (email) for the token
     * @return the generated expired token
     */
    public String generateExpiredToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 10)) // Issued 10 minutes ago
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 5)) // Expired 5 minutes ago
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
