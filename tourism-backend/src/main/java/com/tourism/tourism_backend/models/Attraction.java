package com.tourism.tourism_backend.models;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity representing an attraction in the system.
 */
@Entity
@Table(name = "attractions")
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 1000)
    private String shortDescription = "Default Description";

    @Column(nullable = false)
    private Double entranceFee;

    @ElementCollection
    private List<String> photos;

    // Default constructor
    public Attraction() {}

    // Constructor with parameters
    public Attraction(String name, String shortDescription) {
        this.name = name;
        this.shortDescription = shortDescription;
    }

    public Attraction(String name, String shortDescription, List<String> photosList) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.photos = photosList;
    }

    public Attraction(String name, String shortDescription, Double entranceFee, List<String> photos) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.shortDescription = shortDescription;  // Using short description as default if necessary
        this.entranceFee = entranceFee;
        this.photos = photos;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
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
