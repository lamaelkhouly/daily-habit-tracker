package org.internship.dailyhabittracker.repository;

import org.internship.dailyhabittracker.domain.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitRepository extends JpaRepository<Habit,Long> {
}
