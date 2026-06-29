package com.example.cityguide.repository;

import com.example.cityguide.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByAttractionId(Long attractionId);
}