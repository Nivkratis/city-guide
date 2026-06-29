package com.example.cityguide.repository;

import com.example.cityguide.model.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {

    @Query(value = "SELECT * FROM attractions a WHERE " +
            "(6371 * acos(cos(radians(:userLat)) * cos(radians(a.latitude)) * " +
            "cos(radians(a.longitude) - radians(:userLon)) + sin(radians(:userLat)) * " +
            "sin(radians(a.latitude)))) <= :radius " +
            "ORDER BY (6371 * acos(cos(radians(:userLat)) * cos(radians(a.latitude)) * " +
            "cos(radians(a.longitude) - radians(:userLon)) + sin(radians(:userLat)) * " +
            "sin(radians(a.latitude)))) ASC",
            nativeQuery = true)
    List<Attraction> findInRadius(@Param("userLat") Double userLat,
                                  @Param("userLon") Double userLon,
                                  @Param("radius") Double radius);
}
