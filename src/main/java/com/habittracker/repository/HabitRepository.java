package com.habittracker.repository;

import com.habittracker.domain.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface HabitRepository extends JpaRepository<Habit, Long>, JpaSpecificationExecutor<Habit> {

    Optional<Habit> findByIdAndUserId(Long id, Long userId);
}
