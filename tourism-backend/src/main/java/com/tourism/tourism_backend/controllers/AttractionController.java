package com.tourism.tourism_backend.controllers;

import com.tourism.tourism_backend.dto.AttractionDetailDTO;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.services.AttractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
