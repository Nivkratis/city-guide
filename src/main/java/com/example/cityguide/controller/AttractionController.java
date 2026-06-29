package com.example.cityguide.controller;

import com.example.cityguide.model.Attraction;
import com.example.cityguide.service.AttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attractions")
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionService attractionService;

    @GetMapping
    public List<Attraction> getAll() {
        return attractionService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(attractionService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<Attraction> searchInRadius(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam Double radius,
            @RequestParam(defaultValue = "0.0") Double minRating,
            @RequestParam(required = false) String category) {
        return attractionService.getInRadiusWithRating(lat, lon, radius, minRating, category);
    }

    @PostMapping
    public Attraction save(@RequestBody Attraction attraction) {
        return attractionService.save(attraction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attractionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
