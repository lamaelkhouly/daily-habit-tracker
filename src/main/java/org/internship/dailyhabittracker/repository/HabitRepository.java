package org.internship.dailyhabittracker.repository;

import org.internship.dailyhabittracker.domain.depricated.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit,Long> {
    List<Habit> findByUserId(Long userId);

    Optional<Habit> findByIdAndUserId(Long id, Long userId);

    List<Habit> findByUserIdAndNameContainingIgnoreCase(Long userId, String name);

    List<Habit> findByUserIdAndCategory(Long userId, String category);

    // US-011 + US-012 combined: search by name AND filter by category together.
    List<Habit> findByUserIdAndNameContainingIgnoreCaseAndCategory(Long userId, String name, String category);
}
