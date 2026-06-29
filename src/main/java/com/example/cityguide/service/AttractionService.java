package com.example.cityguide.service;

import com.example.cityguide.model.Attraction;
import com.example.cityguide.repository.AttractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttractionService {

    private final AttractionRepository attractionRepository;

    public List<Attraction> getAll() {
        return attractionRepository.findAll();
    }

    public Attraction getById(Long id) {
        return attractionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Достопримечательность не найдена"));
    }

    public List<Attraction> getInRadiusWithRating(Double lat, Double lon, Double radius, Double minRating, String category) {
        List<Attraction> attractionsInRadius = attractionRepository.findInRadius(lat, lon, radius);

        return attractionsInRadius.stream()
                .filter(attraction -> attraction.getAverageRating() >= minRating)
                .filter(attraction -> category == null || category.isBlank() ||
                        attraction.getCategory().equalsIgnoreCase(category.trim()))
                .limit(10)
                .collect(Collectors.toList());
    }

    public Attraction save(Attraction attraction) {
        return attractionRepository.save(attraction);
    }

    public void delete(Long id) {
        attractionRepository.deleteById(id);
    }
}