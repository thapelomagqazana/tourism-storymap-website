package com.tourism.tourism_backend.repositories;

import com.tourism.tourism_backend.models.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Attraction entity.
 */
@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
}
