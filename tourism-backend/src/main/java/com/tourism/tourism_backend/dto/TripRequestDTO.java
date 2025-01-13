package com.tourism.tourism_backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO for trip creation requests.
 */
public class TripRequestDTO {

    @NotNull(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotNull(message = "Duration is required")
    @NotEmpty(message = "Duration cannot be empty")
    private List<String> duration; // E.g., ["Day 1", "Day 2", ...]

    @NotNull(message = "Attraction IDs are required")
    private List<Long> attractionIds;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDuration() {
        return duration;
    }

    public void setDuration(List<String> duration) {
        this.duration = duration;
    }

    public List<Long> getAttractionIds() {
        return attractionIds;
    }

    public void setAttractionIds(List<Long> attractionIds) {
        this.attractionIds = attractionIds;
    }
}
