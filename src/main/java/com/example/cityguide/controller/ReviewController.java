package com.example.cityguide.controller;

import com.example.cityguide.model.Review;
import com.example.cityguide.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/attraction/{attractionId}")
    public List<Review> getByAttraction(@PathVariable Long attractionId) {
        return reviewService.getReviewsByAttraction(attractionId);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReview(
            @RequestParam Long attractionId,
            @RequestParam Long userId,
            @RequestParam String content,
            @RequestParam Integer rating) {
        try {
            Review review = reviewService.addReview(attractionId, userId, content, rating);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok("Отзыв успешно удален");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
