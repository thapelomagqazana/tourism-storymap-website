package com.tourism.tourism_backend.services;

import com.tourism.tourism_backend.dto.AttractionDetailDTO;
import com.tourism.tourism_backend.exceptions.ResourceNotFoundException;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.repositories.AttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service class for handling attraction-related operations.
 */
@Service
public class AttractionService {

    @Autowired
    private AttractionRepository attractionRepository;

    /**
     * Retrieves all attractions from the database.
     *
     * @return a list of all attractions
     */
    public List<Attraction> getAllAttractions() {
        return attractionRepository.findAll();
    }

    /**
     * Retrieves detailed information for a specific attraction by its ID.
     *
     * @param id the ID of the attraction to retrieve
     * @return an AttractionDetailDTO containing detailed information
     * @throws ResourceNotFoundException if the attraction is not found
     */
    public AttractionDetailDTO getAttractionById(Long id) {
        // Find attraction by ID or throw exception if not found
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attraction not found with id: " + id));

        // Return a DTO with detailed attraction information
        return new AttractionDetailDTO(
                attraction.getName(),
                attraction.getShortDescription(),
                attraction.getEntranceFee(),
                attraction.getPhotos()
        );
    }

    /**
     * Adds a new attraction to the database.
     *
     * @param attractionDTO the DTO containing attraction details
     */
    @Transactional
    public void addAttraction(AttractionDetailDTO attractionDTO) {

        Attraction attraction = new Attraction();
        attraction.setName(attractionDTO.getName().trim());
        attraction.setShortDescription(attractionDTO.getDescription().trim());
        attraction.setEntranceFee(attractionDTO.getEntranceFee());
        attraction.setPhotos(attractionDTO.getPhotos());

        attractionRepository.save(attraction);
    }

    /**
     * Updates an existing attraction by ID.
     *
     * @param id the ID of the attraction to update
     * @param attractionDTO the updated attraction details
     * @return the updated Attraction entity
     * @throws ResourceNotFoundException if the attraction is not found
     */
    public Attraction updateAttraction(Long id, AttractionDetailDTO attractionDTO) {
        Attraction attraction = attractionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attraction not found with ID: " + id));

    // Check if all fields are missing
    if ((attractionDTO.getName() == null || attractionDTO.getName().trim().isEmpty()) && 
        (attractionDTO.getDescription() == null || attractionDTO.getDescription().trim().isEmpty()) &&
        (attractionDTO.getEntranceFee() == null) && (attractionDTO.getPhotos() == null || attractionDTO.getPhotos().isEmpty())) {
        throw new IllegalArgumentException("At least one field is required for update");
    }

    if (attractionDTO.getEntranceFee() != null && attractionDTO.getEntranceFee() < 0) {
        throw new IllegalArgumentException("Entrance fee must be a positive number");
    }

    // Update only if new values are provided
    if (attractionDTO.getName() != null && !attractionDTO.getName().isEmpty()) {
        attraction.setName(attractionDTO.getName());
    }
    if (attractionDTO.getDescription() != null && !attractionDTO.getDescription().isEmpty()) {
        attraction.setShortDescription(attractionDTO.getDescription());
    }
    if (attractionDTO.getEntranceFee() != null) {
        attraction.setEntranceFee(attractionDTO.getEntranceFee());
    }
    if (attractionDTO.getPhotos() != null) {
        attraction.setPhotos(attractionDTO.getPhotos());
    }

        return attractionRepository.save(attraction);
    }

    /**
     * Deletes an attraction by ID.
     *
     * @param id the ID of the attraction to delete
     * @throws ResourceNotFoundException if the attraction with the given ID is not found
     */
    @Transactional
    public void deleteAttractionById(Long id) {
        if (!attractionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attraction not found with ID: " + id);
        }
        attractionRepository.deleteById(id);
    }
    
}
