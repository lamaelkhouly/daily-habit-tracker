package org.internship.dailyhabittracker.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Returned by POST /habits/{id}/complete (US-008).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitCompletionResponse {

    private Long habitId;
    private LocalDate completedDate;
    private LocalDateTime completedAt;
}