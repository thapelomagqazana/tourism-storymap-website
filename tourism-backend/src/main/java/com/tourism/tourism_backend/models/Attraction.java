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
    private String shortDescription;

    @Column(nullable = false)
    private Double entranceFee;

    @ElementCollection
    private List<String> photos;

    @Column(nullable = false)
    private int trafficCount = 0; // Initialize traffic count to 0

    // Default constructor
    public Attraction() {}

    // Constructor with essential fields
    public Attraction(String name, String shortDescription) {
        this.name = name;
        this.shortDescription = shortDescription;
    }

    // Constructor with photos list
    public Attraction(String name, String shortDescription, List<String> photos) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.photos = photos;
    }

    // Constructor with entrance fee and photos list
    public Attraction(String name, String shortDescription, Double entranceFee, List<String> photos) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.entranceFee = entranceFee;
        this.photos = photos;
    }

    // Constructor with all fields including traffic count
    public Attraction(String name, String shortDescription, Double entranceFee, List<String> photos, int trafficCount) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.entranceFee = entranceFee;
        this.photos = photos;
        this.trafficCount = trafficCount;
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

    public int getTrafficCount() {
        return trafficCount;
    }

    public void setTrafficCount(int trafficCount) {
        this.trafficCount = trafficCount;
    }
}
