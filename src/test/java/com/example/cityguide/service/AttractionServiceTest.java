package com.example.cityguide.service;

import com.example.cityguide.model.Attraction;
import com.example.cityguide.repository.AttractionRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttractionServiceTest {

    @Mock
    private AttractionRepository attractionRepository;

    @InjectMocks
    private AttractionService attractionService;

    private Attraction attraction1;
    private Attraction attraction2;

    @BeforeEach
    void setUp() {
        attraction1 = new Attraction();
        attraction1.setId(1L);
        attraction1.setName("Эрмитаж");
        attraction1.setCategory("Музей");
        attraction1.setAverageRating(4.9);

        attraction2 = new Attraction();
        attraction2.setId(2L);
        attraction2.setName("Неинтересное место");
        attraction2.setCategory("Парк");
        attraction2.setAverageRating(3.2);
    }

    @Test
    void getAll_Success_ShouldReturnList() {
        when(attractionRepository.findAll()).thenReturn(Arrays.asList(attraction1, attraction2));

        List<Attraction> result = attractionService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(attractionRepository, times(1)).findAll();
    }

    @Test
    void getById_Success_ShouldReturnAttraction() {
        when(attractionRepository.findById(1L)).thenReturn(Optional.of(attraction1));

        Attraction result = attractionService.getById(1L);

        assertNotNull(result);
        assertEquals("Эрмитаж", result.getName());
    }

    @Test
    void getById_NotFound_ShouldThrowException() {
        when(attractionRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            attractionService.getById(999L);
        });

        assertEquals("Достопримечательность не найдена", exception.getMessage());
    }

    @Test
    void getInRadiusWithRating_ShouldFilterByMinRating() {
        when(attractionRepository.findInRadius(59.93, 30.31, 5.0))
                .thenReturn(Arrays.asList(attraction1, attraction2));

        List<Attraction> filteredResult = attractionService.getInRadiusWithRating(59.93, 30.31, 5.0, 4.0, null);

        assertNotNull(filteredResult);
        assertEquals(1, filteredResult.size());
        assertEquals("Эрмитаж", filteredResult.get(0).getName());
        verify(attractionRepository, times(1)).findInRadius(59.93, 30.31, 5.0);
    }

    @Test
    void getInRadiusWithRating_ShouldFilterByCategory() {
        when(attractionRepository.findInRadius(59.93, 30.31, 5.0))
                .thenReturn(Arrays.asList(attraction1, attraction2));

        List<Attraction> filteredResult = attractionService.getInRadiusWithRating(59.93, 30.31, 5.0, 0.0, "Парк");

        assertNotNull(filteredResult);
        assertEquals(1, filteredResult.size());
        assertEquals("Неинтересное место", filteredResult.get(0).getName());
        assertEquals("Парк", filteredResult.get(0).getCategory());
    }

    @Test
    void getInRadiusWithRating_ShouldFilterByCategory_CaseInsensitiveAndTrim() {
        when(attractionRepository.findInRadius(59.93, 30.31, 5.0))
                .thenReturn(Arrays.asList(attraction1, attraction2));

        List<Attraction> filteredResult = attractionService.getInRadiusWithRating(59.93, 30.31, 5.0, 0.0, "  музей ");

        assertNotNull(filteredResult);
        assertEquals(1, filteredResult.size());
        assertEquals("Эрмитаж", filteredResult.get(0).getName());
    }

    @Test
    void save_Success_ShouldReturnSavedAttraction() {
        when(attractionRepository.save(any(Attraction.class))).thenReturn(attraction1);

        Attraction saved = attractionService.save(attraction1);

        assertNotNull(saved);
        assertEquals("Эрмитаж", saved.getName());
        verify(attractionRepository, times(1)).save(attraction1);
    }

    @Test
    void delete_Success_ShouldInvokeRepositoryDelete() {
        doNothing().when(attractionRepository).deleteById(1L);

        assertDoesNotThrow(() -> attractionService.delete(1L));

        verify(attractionRepository, times(1)).deleteById(1L);
    }
}
