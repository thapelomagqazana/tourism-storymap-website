package com.tourism.tourism_backend.services;

import com.tourism.tourism_backend.dto.AnalyticsResponseDTO;
import com.tourism.tourism_backend.models.Attraction;
import com.tourism.tourism_backend.repositories.AttractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private AttractionRepository attractionRepository;

    /**
     * Retrieves analytics data including total clicks and most visited attractions.
     *
     * @return AnalyticsResponseDTO containing total clicks and most visited attractions
     */
    public AnalyticsResponseDTO getAnalytics() {
        List<Attraction> allAttractions = attractionRepository.findAll();
    
        // Calculate total clicks
        int totalClicks = allAttractions.stream()
                .mapToInt(Attraction::getTrafficCount)
                .sum();
    
        // Get most visited attractions (exclude attractions with zero traffic count)
        List<String> mostVisitedAttractions = allAttractions.stream()
                .filter(attraction -> attraction.getTrafficCount() > 0) // Exclude zero traffic count
                .sorted((a1, a2) -> Integer.compare(a2.getTrafficCount(), a1.getTrafficCount()))
                .limit(5) // Limit to top 5 attractions
                .map(Attraction::getName)
                .collect(Collectors.toList());
    
        return new AnalyticsResponseDTO(totalClicks, mostVisitedAttractions);
    }    
}
