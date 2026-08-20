package com.habittracker.service.impl;

import com.habittracker.domain.Habit;
import com.habittracker.domain.HabitCategory;
import com.habittracker.domain.HabitCompletion;
import com.habittracker.domain.User;
import com.habittracker.exception.HabitNotFoundException;
import com.habittracker.generated.model.*;
import com.habittracker.mapper.HabitMapper;
import com.habittracker.repository.HabitCompletionRepository;
import com.habittracker.repository.HabitRepository;
import com.habittracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceImplTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCompletionRepository completionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HabitMapper habitMapper;

    @InjectMocks
    private HabitServiceImpl habitService;

    private User user;
    private Habit habit;
    private HabitRequest habitRequest;
    private HabitResponse habitResponse;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .username("lama")
                .build();

        habit = Habit.builder()
                .id(1L)
                .name("Drink Water")
                .description("2 Liters")
                .category(HabitCategory.HEALTH)
                .user(user)
                .build();

        habitRequest = new HabitRequest();
        habitRequest.setName("Drink Water");
        habitRequest.setDescription("2 Liters");
        habitRequest.setCategory(
                com.habittracker.generated.model.HabitCategory.HEALTH
        );

        habitResponse = new HabitResponse();
        habitResponse.setId(1L);
        habitResponse.setName("Drink Water");
    }

    @Test
    void shouldCreateHabitSuccessfully() {

        // Arrange
        when(userRepository.findByUsername("lama"))
                .thenReturn(Optional.of(user));

        when(completionRepository.existsByHabitIdAndCompletedDate(
                anyLong(),
                any(LocalDate.class)))
                .thenReturn(false);

        when(habitMapper.toResponse(any(), anyBoolean(), anyInt()))
                .thenReturn(habitResponse);
        when(habitRepository.save(any(Habit.class)))
                .thenAnswer(invocation -> {
                    Habit savedHabit = invocation.getArgument(0);
                    savedHabit.setId(1L);
                    return savedHabit;
                });

        // Act
        HabitResponse response =
                habitService.createHabit("lama", habitRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Drink Water", response.getName());

        verify(habitRepository).save(any(Habit.class));
    }

    @Test
    void shouldReturnHabitById() {

        // Arrange
        when(userRepository.findByUsername("lama"))
                .thenReturn(Optional.of(user));

        when(habitRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(habit));

        when(completionRepository.existsByHabitIdAndCompletedDate(
                anyLong(),
                any(LocalDate.class)))
                .thenReturn(false);

        when(habitMapper.toResponse(any(), anyBoolean(), anyInt()))
                .thenReturn(habitResponse);

        // Act
        HabitResponse response =
                habitService.getHabitById("lama", 1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowHabitNotFoundException() {

        // Arrange
        when(userRepository.findByUsername("lama"))
                .thenReturn(Optional.of(user));

        when(habitRepository.findByIdAndUserId(99L, 1L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                HabitNotFoundException.class,
                () -> habitService.getHabitById("lama", 99L)
        );
    }

    @Test
    void shouldDeleteHabit() {

        // Arrange
        when(userRepository.findByUsername("lama"))
                .thenReturn(Optional.of(user));

        when(habitRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(habit));

        // Act
        habitService.deleteHabit("lama", 1L);

        // Assert
        verify(habitRepository).delete(habit);
    }

    @Test
    void shouldCompleteHabit() {

        // Arrange
        when(userRepository.findByUsername("lama"))
                .thenReturn(Optional.of(user));

        when(habitRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(habit));

        when(completionRepository.existsByHabitIdAndCompletedDate(
                anyLong(),
                any(LocalDate.class)))
                .thenReturn(false);

        when(habitMapper.toResponse(any(), anyBoolean(), anyInt()))
                .thenReturn(habitResponse);

        CompletionRequest request = new CompletionRequest();
        request.setDate(LocalDate.now());

        // Act
        habitService.completeHabit("lama", 1L, request);

        // Assert
        verify(completionRepository)
                .save(any(HabitCompletion.class));
    }

    @Test
    void shouldNotCreateDuplicateCompletion() {

        // Arrange
        when(userRepository.findByUsername("lama"))
                .thenReturn(Optional.of(user));

        when(habitRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(habit));

        when(completionRepository.existsByHabitIdAndCompletedDate(
                anyLong(),
                any(LocalDate.class)))
                .thenReturn(true);

        when(habitMapper.toResponse(any(), anyBoolean(), anyInt()))
                .thenReturn(habitResponse);

        CompletionRequest request = new CompletionRequest();
        request.setDate(LocalDate.now());

        // Act
        habitService.completeHabit("lama", 1L, request);

        // Assert
        verify(completionRepository, never())
                .save(any(HabitCompletion.class));
    }

    @Test
    void shouldReturnProgress() {

        // Arrange
        when(userRepository.findByUsername("lama"))
                .thenReturn(Optional.of(user));

        when(habitRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(habit));

        List<HabitCompletion> completions = List.of(
                HabitCompletion.builder()
                        .completedDate(LocalDate.now())
                        .build(),
                HabitCompletion.builder()
                        .completedDate(LocalDate.now().minusDays(1))
                        .build()
        );

        when(completionRepository
                .findByHabitIdAndCompletedDateBetweenOrderByCompletedDateDesc(
                        anyLong(),
                        any(LocalDate.class),
                        any(LocalDate.class)))
                .thenReturn(completions);

        // Act
        ProgressResponse response =
                habitService.getProgress(
                        "lama",
                        1L,
                        ProgressPeriod.WEEKLY
                );

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getTotalCompletions());
        assertEquals(7, response.getExpectedDays());
    }
}