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
}
