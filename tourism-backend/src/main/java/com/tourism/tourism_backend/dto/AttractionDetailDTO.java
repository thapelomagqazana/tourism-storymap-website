package com.tourism.tourism_backend.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for returning detailed attraction information.
 */
public class AttractionDetailDTO {

    @NotEmpty(message = "Name is required")
    private String name;
    
    @NotEmpty(message = "Description is required")
    private String description;

    @NotNull(message = "Entrance fee is required")
    @Min(value = 0, message = "Entrance fee must be a positive number")
    private Double entranceFee;

    private List<String> photos;

    /**
     * Constructor for creating an AttractionDetailDTO.
     *
     * @param name        the name of the attraction
     * @param description the description of the attraction
     * @param photos      the list of photo URLs for the attraction
     */
    public AttractionDetailDTO(String name, String description, 
    Double entranceFee, List<String> photos) {
        this.name = name;
        this.description = description;
        this.entranceFee = entranceFee;
        this.photos = photos;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getEntranceFee() {
        return entranceFee;
    }

    public void setEntranceFee(Double entranceFee) {
        this.entranceFee = entranceFee;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
