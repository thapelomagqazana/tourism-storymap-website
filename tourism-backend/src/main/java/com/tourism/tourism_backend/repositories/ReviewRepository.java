package com.tourism.tourism_backend.repositories;

import com.tourism.tourism_backend.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByAttractionId(Long attractionId);
}
