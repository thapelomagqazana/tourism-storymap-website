package com.tourism.tourism_backend.dto;

import java.util.List;

public class AnalyticsResponseDTO {

    private int totalClicks;
    private List<String> mostVisitedAttractions;

    // Constructor
    public AnalyticsResponseDTO(int totalClicks, List<String> mostVisitedAttractions) {
        this.totalClicks = totalClicks;
        this.mostVisitedAttractions = mostVisitedAttractions;
    }

    // Getters and Setters
    public int getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(int totalClicks) {
        this.totalClicks = totalClicks;
    }

    public List<String> getMostVisitedAttractions() {
        return mostVisitedAttractions;
    }

    public void setMostVisitedAttractions(List<String> mostVisitedAttractions) {
        this.mostVisitedAttractions = mostVisitedAttractions;
    }
}