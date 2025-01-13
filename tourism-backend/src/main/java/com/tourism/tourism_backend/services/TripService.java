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

    /**
     * Updates an existing trip plan.
     *
     * @param id          the ID of the trip to be updated
     * @param tripRequest the updated trip data
     * @return the updated trip
     * @throws IllegalArgumentException if the trip or any attraction ID is not found
     */
    public Trip updateTrip(Long id, TripRequestDTO tripRequest) {
        // Retrieve the trip by ID
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + id));

        // Remove duplicate attraction IDs
        List<Long> uniqueAttractionIds = tripRequest.getAttractionIds().stream()
                .distinct()
                .collect(Collectors.toList());

        // Retrieve the attractions by unique IDs
        List<Attraction> attractions = uniqueAttractionIds.stream()
                .map(attractionId -> attractionRepository.findById(attractionId)
                        .orElseThrow(() -> new IllegalArgumentException("Attraction not found with ID: " + attractionId)))
                .collect(Collectors.toList());

        // Update the trip fields
        trip.setName(tripRequest.getName());
        trip.setDays(tripRequest.getDuration());
        trip.setAttractions(attractions);

        // Save and return the updated trip
        return tripRepository.save(trip);
    }

    /**
     * Deletes a trip by its ID.
     *
     * @param id the ID of the trip to be deleted
     * @throws IllegalArgumentException if the trip is not found
     */
    public void deleteTrip(Long id) {
        if (!tripRepository.existsById(id)) {
            throw new IllegalArgumentException("Trip not found with ID: " + id);
        }
        tripRepository.deleteById(id);
    }
}
