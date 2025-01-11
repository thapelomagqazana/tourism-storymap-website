package com.tourism.tourism_backend.controllers;

import com.tourism.tourism_backend.dto.AttractionDetailDTO;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.services.AttractionService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for handling attraction-related requests.
 */
@RestController
@RequestMapping("/api/attractions")
public class AttractionController {

    @Autowired
    private AttractionService attractionService;

    /**
     * Retrieves a list of all attractions with basic details.
     *
     * @return a ResponseEntity containing a list of attractions
     */
    @GetMapping
    public ResponseEntity<List<Attraction>> getAllAttractions() {
        List<Attraction> attractions = attractionService.getAllAttractions();
        return ResponseEntity.ok(attractions);
    }

    /**
     * GET endpoint to retrieve detailed information for a specific attraction.
     *
     * @param id the ID of the attraction to retrieve
     * @return a ResponseEntity containing detailed attraction information
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttractionDetailDTO> getAttraction(@PathVariable Long id) {
        AttractionDetailDTO attractionDetail = attractionService.getAttractionById(id);
        return ResponseEntity.ok(attractionDetail);
    }

    /**
     * Adds a new attraction. Admin only.
     *
     * @param attractionDTO the attraction details
     * @return ResponseEntity with success message or error
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> addAttraction(@Valid @RequestBody AttractionDetailDTO attractionDTO) {
        
        String name = attractionDTO.getName();
        
        // Check for null or whitespace-only name
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Name is required"));
        }
        
        attractionService.addAttraction(attractionDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Attraction added successfully"));
    }
}
