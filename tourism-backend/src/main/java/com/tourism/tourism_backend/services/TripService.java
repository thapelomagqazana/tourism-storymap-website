package com.tourism.tourism_backend.services;

import com.tourism.tourism_backend.models.Trip;
import com.tourism.tourism_backend.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for handling business logic related to trips.
 */
@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

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
}
