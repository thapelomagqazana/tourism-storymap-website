package com.tourism.tourism_backend.services;

import com.tourism.tourism_backend.repositories.BlacklistedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for scheduling cleanup of expired tokens.
 */
@Service
public class TokenCleanupService {

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    /**
     * Scheduled task to remove expired tokens every hour.
     */
    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 ms)
    public void cleanUpExpiredTokens() {
        blacklistedTokenRepository.deleteAll(
                blacklistedTokenRepository.findAll().stream()
                        .filter(token -> token.getExpiryDate().isBefore(LocalDateTime.now()))
                        .toList()
        );
    }
}