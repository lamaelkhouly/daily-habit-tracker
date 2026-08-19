package org.internship.dailyhabittracker.domain.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Records a single day's completion of a habit.
 * The unique constraint on (habit_id, completedDate) enforces the
 * "one completion per calendar day" rule from US-008 at the database level.
 * See also US-009 (Daily Progress) and US-010 (Weekly/Monthly Progress).
 */
@Entity
@Table(
        name = "habit_completions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"habit_id", "completedDate"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    // The calendar day the habit was completed for (e.g. "today").
    @Column(nullable = false)
    private LocalDate completedDate;

    // Server-set timestamp — never accepted from the client (US-008).
    @Column(nullable = false, updatable = false)
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.completedAt = LocalDateTime.now();
        if (this.completedDate == null) {
            this.completedDate = LocalDate.now();
        }
    }
}