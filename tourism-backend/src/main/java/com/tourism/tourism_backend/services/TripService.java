package com.tourism.tourism_backend.services;

import com.tourism.tourism_backend.models.Trip;
import com.tourism.tourism_backend.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
