package com.tourism.tourism_backend.controllers;

import com.tourism.tourism_backend.services.AttractionTrafficService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for handling attraction traffic count.
 */
@RestController
@RequestMapping("/api/attractions")
public class AttractionTrafficController {

    @Autowired
    private AttractionTrafficService attractionTrafficService;

    /**
     * POST endpoint to increment the traffic count for an attraction.
     *
     * @param id the ID of the attraction
     * @return ResponseEntity with the updated traffic count or an error message
     */
    @PostMapping("/{id}/traffic")
    public ResponseEntity<?> incrementTrafficCount(@PathVariable Long id) {
        Optional<Integer> updatedTrafficCount = attractionTrafficService.incrementTrafficCount(id);

        if (updatedTrafficCount.isPresent()) {
            // Return the updated traffic count
            return ResponseEntity.ok("{\"trafficCount\": " + updatedTrafficCount.get() + "}");
        }

        // Return 404 if attraction not found
        return ResponseEntity.status(404).body("{\"error\": \"Attraction not found with ID: " + id + "\"}");
    }
}
