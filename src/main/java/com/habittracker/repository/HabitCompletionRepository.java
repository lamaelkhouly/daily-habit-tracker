package com.habittracker.repository;

import com.habittracker.domain.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {

    Optional<HabitCompletion> findByHabitIdAndCompletedDate(Long habitId, LocalDate completedDate);

    boolean existsByHabitIdAndCompletedDate(Long habitId, LocalDate completedDate);

    List<HabitCompletion> findByHabitIdAndCompletedDateBetweenOrderByCompletedDateDesc(
            Long habitId, LocalDate start, LocalDate end);

    List<HabitCompletion> findByHabitIdOrderByCompletedDateDesc(Long habitId);
}
