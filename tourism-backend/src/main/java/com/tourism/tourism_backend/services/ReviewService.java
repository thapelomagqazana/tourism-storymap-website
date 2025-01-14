package com.tourism.tourism_backend.services;

import com.tourism.tourism_backend.dto.ReviewRequestDTO;
import com.tourism.tourism_backend.dto.ReviewResponseDTO;
import com.tourism.tourism_backend.models.AppUser;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.models.Review;
import com.tourism.tourism_backend.repositories.AttractionRepository;
import com.tourism.tourism_backend.repositories.ReviewRepository;
import com.tourism.tourism_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Adds a new review for an attraction.
     *
     * @param attractionId  ID of the attraction
     * @param reviewRequest Review request containing rating and comment
     * @param auth          Authentication object to get the current user
     * @return The saved review
     */
    public Long addReview(Long attractionId, ReviewRequestDTO reviewRequest, Authentication auth) {
        // Find the logged-in user by email
        String email = auth.getName();
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find the attraction by ID
        Attraction attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new IllegalArgumentException("Attraction not found with ID: " + attractionId));

        // Create a new review object
        Review review = new Review(attraction, user, reviewRequest.getRating(), reviewRequest.getComment());

        // Save the review
        Review savedReview = reviewRepository.save(review);

        // Return the saved review's ID
        return savedReview.getId();
    }

    /**
     * Retrieves all reviews for a specific attraction.
     *
     * @param attractionId the ID of the attraction
     * @return List of ReviewResponseDTO objects
     */
    public List<ReviewResponseDTO> getReviewsByAttractionId(Long attractionId) {
        List<Review> reviews = reviewRepository.findByAttractionId(attractionId);

        // Check if the attraction exists
        if (!attractionRepository.existsById(attractionId)) {
            throw new IllegalArgumentException("Attraction not found with ID: " + attractionId);
        }
        
        return reviews.stream()
                .map(review -> new ReviewResponseDTO(
                        review.getUser().getName(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt()))
                .collect(Collectors.toList());
    }
}