package com.tourism.tourism_backend.services;

import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.repositories.AttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for handling attraction traffic count operations.
 */
@Service
public class AttractionTrafficService {

    @Autowired
    private AttractionRepository attractionRepository;

    /**
     * Increments the traffic count for an attraction.
     *
     * @param id the ID of the attraction
     * @return the updated traffic count or an error message
     */
    public Optional<Integer> incrementTrafficCount(Long id) {

        // Validate if the ID is a positive numeric value
        // if (!String.valueOf(id).matches("\\d+")) {
        //     throw new IllegalArgumentException("Invalid ID");
        // }

        // Find the attraction by ID
        Optional<Attraction> attractionOptional = attractionRepository.findById(id);

        if (attractionOptional.isPresent()) {
            Attraction attraction = attractionOptional.get();
            // Increment the traffic count
            attraction.setTrafficCount(attraction.getTrafficCount() + 1);
            // Save the updated attraction
            attractionRepository.save(attraction);
            // Return the updated traffic count
            return Optional.of(attraction.getTrafficCount());
        }

        // Return empty if attraction not found
        return Optional.empty();
    }
}
