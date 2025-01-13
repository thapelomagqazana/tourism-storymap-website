package com.tourism.tourism_backend.controllers;

import com.tourism.tourism_backend.dto.TripRequestDTO;
import com.tourism.tourism_backend.models.Trip;
import com.tourism.tourism_backend.services.TripService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Controller for handling HTTP requests related to trips.
 */
@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    /**
     * GET endpoint to retrieve all predefined trip plans.
     *
     * @return ResponseEntity containing a list of Trip objects
     */
    @GetMapping
    public ResponseEntity<List<Trip>> getTrips() {
        // Retrieve all trips using the service layer
        List<Trip> trips = tripService.getAllTrips();

        // Return the list of trips in the response with HTTP 200 status
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTripDetails(@PathVariable Long id) {
        Optional<Trip> tripOptional = tripService.findTripById(id);

        if (tripOptional.isEmpty()) {
            return ResponseEntity.status(404).body("{\"error\": \"Trip not found with ID: " + id + "\"}");
        }

        return ResponseEntity.ok(tripOptional.get());
    }

    /**
     * POST endpoint to add a new trip plan (Admin only).
     *
     * @param tripRequest the request body containing trip details
     * @return ResponseEntity with success message or error
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addTripPlan(@RequestBody TripRequestDTO tripRequest) {
        Trip createdTrip = tripService.createTrip(tripRequest);
        return ResponseEntity.status(201).body("{\"message\": \"Trip created successfully\", \"tripId\": " + createdTrip.getId() + "}");
    }

    /**
     * PUT endpoint to update an existing trip plan.
     * Only accessible to admin users.
     *
     * @param id          the ID of the trip to be updated
     * @param tripRequest the updated trip data
     * @return ResponseEntity with the updated trip data or an error message
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTrip(@PathVariable Long id, @Valid @RequestBody TripRequestDTO tripRequest) {
        Trip updatedTrip = tripService.updateTrip(id, tripRequest);
        return ResponseEntity.ok(updatedTrip);
    }
    

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTrip(@PathVariable Long id) {
        try {
            tripService.deleteTrip(id);
            return ResponseEntity.ok("{\"message\": \"Trip deleted successfully\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
