package org.internship.dailyhabittracker.repository;

import org.internship.dailyhabittracker.domain.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit,Long> {
    List<Habit> findByUserId(Long userId);

}
