package com.tourism.tourism_backend.models;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity representing a trip in the system.
 */
@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @ElementCollection
    private List<String> days; // List of days with descriptions

    @ManyToMany
    @JoinTable(
        name = "trip_attractions",
        joinColumns = @JoinColumn(name = "trip_id"),
        inverseJoinColumns = @JoinColumn(name = "attraction_id")
    )
    private List<Attraction> attractions;

    // Default constructor
    public Trip() {}

    // Constructor with all fields
    public Trip(String name, List<String> days, List<Attraction> attractions) {
        this.name = name;
        this.days = days;
        this.attractions = attractions;
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

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public List<Attraction> getAttractions() {
        return attractions;
    }

    public void setAttractions(List<Attraction> attractions) {
        this.attractions = attractions;
    }
}
