package com.example.cityguide.service;

import com.example.cityguide.model.User;
import com.example.cityguide.model.Review;
import com.example.cityguide.model.Attraction;
import com.example.cityguide.repository.UserRepository;
import com.example.cityguide.repository.ReviewRepository;
import com.example.cityguide.repository.AttractionRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AttractionRepository attractionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User testUser;
    private Attraction testAttraction;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testAttraction = new Attraction();
        testAttraction.setId(1L);
        testAttraction.setName("Исаакиевский собор");
        testAttraction.setAverageRating(0.0);

        testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("piter_explorer");

        testReview = new Review();
        testReview.setId(1L);
        testReview.setAttraction(testAttraction);
        testReview.setUser(testUser);
        testReview.setContent("Прекрасное место для прогулок");
        testReview.setRating(5);
    }

    @Test
    void getAllReviews_Success_ShouldReturnList() {
        Review secondReview = new Review();
        secondReview.setContent("Не очень понравилось, шумно");
        secondReview.setRating(3);

        when(reviewRepository.findAll()).thenReturn(Arrays.asList(testReview, secondReview));

        List<Review> reviews = reviewService.getAllReviews();

        assertNotNull(reviews);
        assertEquals(2, reviews.size());
    }

    @Test
    void getReviewsByAttraction_Success_ShouldReturnFilteredList() {

        when(reviewRepository.findByAttractionId(1L)).thenReturn(Arrays.asList(testReview));

        List<Review> reviews = reviewService.getReviewsByAttraction(1L);

        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        assertEquals("Прекрасное место для прогулок", reviews.get(0).getContent());
        verify(reviewRepository, times(1)).findByAttractionId(1L);
    }

    @Test
    void getReviewsByAttraction_NotFound_ShouldReturnEmptyList() {

        when(reviewRepository.findByAttractionId(999L)).thenReturn(java.util.Collections.emptyList());

        List<Review> reviews = reviewService.getReviewsByAttraction(999L);

        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());
        verify(reviewRepository, times(1)).findByAttractionId(999L);
    }

    @Test
    void addReview_Success_ShouldReturnSavedReviewAndRecalculateRating() {

        when(attractionRepository.findById(1L)).thenReturn(Optional.of(testAttraction));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        when(reviewRepository.findByAttractionId(1L)).thenReturn(Arrays.asList(testReview));
        when(attractionRepository.save(any(Attraction.class))).thenReturn(testAttraction);

        Review createdReview = reviewService.addReview(1L, 1L, "Прекрасное место для прогулок", 5);

        assertNotNull(createdReview);
        assertEquals(1L, createdReview.getId());
        assertEquals(5, createdReview.getRating());
        assertEquals(5.0, testAttraction.getAverageRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(attractionRepository, times(1)).save(testAttraction);
    }

    @Test
    void addReview_InvalidRating_ShouldThrowException() {

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.addReview(1L, 1L, "Крутое место!", 6);
        });

        assertEquals("Оценка должна быть от 1 до 5", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(attractionRepository, never()).save(any(Attraction.class));
    }

    @Test
    void addReview_AttractionNotFound_ShouldThrowException() {

        when(attractionRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.addReview(1L, 1L, "Крутое место!", 5);
        });

        assertEquals("Достопримечательность не найдена", exception.getMessage());
        verify(userRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_Success_ShouldInvokeRepositoryDelete() {

        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);

        assertDoesNotThrow(() -> reviewService.deleteReview(1L));

        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteReview_NotFound_ShouldThrowRuntimeException() {

        when(reviewRepository.existsById(999L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(999L);
        });

        assertEquals("Отзыв с ID 999 не найден", exception.getMessage());
        verify(reviewRepository, never()).deleteById(anyLong());
    }
}
