package com.example.cityguide.service;

import com.example.cityguide.model.Attraction;
import com.example.cityguide.model.Review;
import com.example.cityguide.model.User;
import com.example.cityguide.repository.AttractionRepository;
import com.example.cityguide.repository.ReviewRepository;
import com.example.cityguide.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AttractionRepository attractionRepository;
    private final UserRepository userRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByAttraction(Long attractionId) {
        return reviewRepository.findByAttractionId(attractionId);
    }

    @Transactional
    public Review addReview(Long attractionId, Long userId, String content, Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Оценка должна быть от 1 до 5");
        }

        Attraction attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> new RuntimeException("Достопримечательность не найдена"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Review review = new Review();
        review.setAttraction(attraction);
        review.setUser(user);
        review.setContent(content);
        review.setRating(rating);
        Review savedReview = reviewRepository.save(review);

        List<Review> allReviews = reviewRepository.findByAttractionId(attractionId);

        double sum = 0;
        for (Review r : allReviews) {
            sum += r.getRating();
        }
        double newAverage = sum / allReviews.size();

        newAverage = Math.round(newAverage * 100.0) / 100.0;
        attraction.setAverageRating(newAverage);
        attractionRepository.save(attraction);

        return savedReview;
    }

    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Отзыв с ID " + id + " не найден");
        }
        reviewRepository.deleteById(id);
    }
}
