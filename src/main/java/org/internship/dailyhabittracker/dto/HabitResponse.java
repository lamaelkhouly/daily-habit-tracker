package org.internship.dailyhabittracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Outward-facing view of a Habit, returned by create/update/list endpoints
 * (US-004, US-005, US-006).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitResponse {

    private Long id;
    private String name;
    private String category;
    private LocalDateTime createdAt;
}