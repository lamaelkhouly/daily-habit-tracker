package com.habittracker.service;

import com.habittracker.domain.HabitCategory;
import com.habittracker.generated.model.CompletionRequest;
import com.habittracker.generated.model.HabitRequest;
import com.habittracker.generated.model.HabitResponse;
import com.habittracker.generated.model.PagedHabitResponse;
import com.habittracker.generated.model.ProgressPeriod;
import com.habittracker.generated.model.ProgressResponse;

public interface HabitService {

    PagedHabitResponse getHabits(String username, String name, HabitCategory category,
                                  Boolean completedToday, int page, int size, String sort);

    HabitResponse createHabit(String username, HabitRequest request);

    HabitResponse getHabitById(String username, Long habitId);

    HabitResponse updateHabit(String username, Long habitId, HabitRequest request);

    void deleteHabit(String username, Long habitId);

    HabitResponse completeHabit(String username, Long habitId, CompletionRequest request);

    ProgressResponse getProgress(String username, Long habitId, ProgressPeriod period);
}
