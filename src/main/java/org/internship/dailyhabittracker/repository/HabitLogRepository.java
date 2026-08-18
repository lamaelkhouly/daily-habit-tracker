package org.internship.dailyhabittracker.repository;

import org.internship.dailyhabittracker.domain.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitLogRepository extends JpaRepository<HabitLog,Long> {
    List<HabitLog> findByHabitId(Long habitId);

}
