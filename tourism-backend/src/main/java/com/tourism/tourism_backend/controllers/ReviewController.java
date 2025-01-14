package com.tourism.tourism_backend.controllers;

import com.tourism.tourism_backend.dto.ReviewRequestDTO;
import com.tourism.tourism_backend.dto.ReviewResponseDTO;
import com.tourism.tourism_backend.models.Review;
import com.tourism.tourism_backend.services.ReviewService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * POST endpoint to add a new review for an attraction.
     *
     * @param id            ID of the attraction
     * @param reviewRequest Review request containing rating and comment
     * @param auth          Authentication object to get the current user
     * @return ResponseEntity with a success message or error
     */
    @PostMapping("/attraction/{id}")
    public ResponseEntity<?> addReviewForAttraction(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDTO reviewRequest,
            Authentication auth) {
        // Add the review using the service and get the review ID
        Long reviewId = reviewService.addReview(id, reviewRequest, auth);
    
        // Return a success message with the review ID
        return ResponseEntity.status(201).body("{\"message\": \"Review added successfully\", \"reviewId\": " + reviewId + "}");
    }

    /**
     * GET endpoint to retrieve all reviews for a specific attraction.
     *
     * @param id the ID of the attraction
     * @return ResponseEntity with the list of reviews or an error message
     */
    @GetMapping("/attraction/{id}")
    public ResponseEntity<?> getAllReviewsForAttraction(@PathVariable Long id) {
        try {
            List<ReviewResponseDTO> reviews = reviewService.getReviewsByAttractionId(id);
            return ResponseEntity.ok(reviews);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body("{\"error\": \"" + ex.getMessage() + "\"}");
        }
    }
}
