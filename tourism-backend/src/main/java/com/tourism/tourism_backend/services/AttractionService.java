package com.tourism.tourism_backend.services;

import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.repositories.AttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
