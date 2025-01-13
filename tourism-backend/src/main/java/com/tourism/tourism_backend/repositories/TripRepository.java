package com.tourism.tourism_backend.repositories;

import com.tourism.tourism_backend.models.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Trip entity.
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    
}
