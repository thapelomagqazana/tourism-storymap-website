package com.tourism.tourism_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tourism.tourism_backend.models.BlacklistedToken;
import com.tourism.tourism_backend.repositories.BlacklistedTokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service for managing blacklisted tokens.
 */
@Service
public class TokenBlacklistService {
    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Blacklists a token by saving it to the database with its expiry date.
     *
     * @param token the JWT token to be blacklisted.
     */
    public void blacklistToken(String token) {
        if (blacklistedTokenRepository.findByToken(token).isEmpty()) {
            LocalDateTime expiryDate = getTokenExpiryDate(token);
            BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiryDate);
            blacklistedTokenRepository.save(blacklistedToken);
        }
    }

    /**
     * Checks if a token is blacklisted.
     *
     * @param token the JWT token to check.
     * @return true if the token is blacklisted, false otherwise.
     */
    public boolean isTokenBlacklisted(String token) {
        Optional<BlacklistedToken> blacklistedToken = blacklistedTokenRepository.findByToken(token);
        return blacklistedToken.isPresent();
    }

    /**
     * Extracts the expiry date from a JWT token.
     *
     * @param token the JWT token.
     * @return the expiry date as LocalDateTime.
     */
    private LocalDateTime getTokenExpiryDate(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return LocalDateTime.ofInstant(claims.getExpiration().toInstant(), java.time.ZoneId.systemDefault());
    }
}
