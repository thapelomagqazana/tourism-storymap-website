package com.tourism.tourism_backend.controllers;

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
}
