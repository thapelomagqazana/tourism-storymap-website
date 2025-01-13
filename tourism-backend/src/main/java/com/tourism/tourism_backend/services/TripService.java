package com.tourism.tourism_backend.services;

import com.tourism.tourism_backend.dto.TripRequestDTO;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.models.Trip;
import com.tourism.tourism_backend.repositories.AttractionRepository;
import com.tourism.tourism_backend.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for handling business logic related to trips.
 */
@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    
    @Autowired
    private AttractionRepository attractionRepository;

    /**
     * Retrieves all trips from the database.
     *
     * @return List of Trip objects.
     */
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    /**
     * Retrieves detailed trip information by ID.
     *
     * @param id the ID of the trip
     * @return an Optional containing the trip if found, or empty if not found
     */
    public Optional<Trip> findTripById(Long id) {
        return tripRepository.findById(id);
    }

    /**
     * Creates a new trip based on the provided request.
     *
     * @param tripRequest the details of the trip to create
     * @return the created Trip object
     */
    public Trip createTrip(TripRequestDTO tripRequest) {
        // Validate attractions
        List<Attraction> attractions = tripRequest.getAttractionIds().stream()
            .map((Long attractionId) -> attractionRepository.findById(attractionId)
                    .orElseThrow(() -> new IllegalArgumentException("Attraction not found with ID: " + attractionId)))
            .collect(Collectors.toList());

        // Create and save the trip
        Trip trip = new Trip(tripRequest.getName(), tripRequest.getDuration(), attractions);
        return tripRepository.save(trip);
    }
}
