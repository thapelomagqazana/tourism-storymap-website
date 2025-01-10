package com.tourism.tourism_backend.dto;

import java.util.List;

/**
 * Data Transfer Object for returning detailed attraction information.
 */
public class AttractionDetailDTO {

    private String name;
    private String description;
    private List<String> photos;

    /**
     * Constructor for creating an AttractionDetailDTO.
     *
     * @param name        the name of the attraction
     * @param description the description of the attraction
     * @param photos      the list of photo URLs for the attraction
     */
    public AttractionDetailDTO(String name, String description, List<String> photos) {
        this.name = name;
        this.description = description;
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

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
