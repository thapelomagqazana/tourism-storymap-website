package com.tourism.tourism_backend.models;

import jakarta.persistence.*;

/**
 * Entity representing an attraction in the system.
 */
@Entity
@Table(name = "attractions")
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String shortDescription;

    // Default constructor
    public Attraction() {}

    // Constructor with parameters
    public Attraction(String name, String shortDescription) {
        this.name = name;
        this.shortDescription = shortDescription;
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
}
