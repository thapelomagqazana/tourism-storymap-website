package com.tourism.tourism_backend.controllers;

import com.tourism.tourism_backend.models.Trip;
import com.tourism.tourism_backend.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
