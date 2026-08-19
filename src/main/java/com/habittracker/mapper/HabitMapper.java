package com.habittracker.mapper;

import com.habittracker.domain.Habit;
import com.habittracker.generated.model.HabitCategory;
import com.habittracker.generated.model.HabitResponse;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class HabitMapper {

    public HabitResponse toResponse(Habit habit, boolean completedToday, int currentStreak) {
        HabitResponse response = new HabitResponse();
        response.setId(habit.getId());
        response.setName(habit.getName());
        response.setDescription(habit.getDescription());
        response.setCategory(HabitCategory.valueOf(habit.getCategory().name()));
        response.setCreatedAt(OffsetDateTime.of(habit.getCreatedAt(), ZoneOffset.UTC));
        response.setCompletedToday(completedToday);
        response.setCurrentStreak(currentStreak);
        return response;
    }
}
