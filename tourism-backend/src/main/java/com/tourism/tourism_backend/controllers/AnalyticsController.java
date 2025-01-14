package com.tourism.tourism_backend.controllers;

import com.tourism.tourism_backend.dto.AnalyticsResponseDTO;
import com.tourism.tourism_backend.services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * GET endpoint to retrieve analytics data.
     *
     * @return ResponseEntity with analytics data or an error message
     */
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsResponseDTO> getAnalytics() {
        AnalyticsResponseDTO analytics = analyticsService.getAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
