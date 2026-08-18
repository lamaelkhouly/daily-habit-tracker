package org.internship.dailyhabittracker.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "HABIT_LOG")
public class HabitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
    @Column(name = "LOG_DATE", nullable = false)
    private Instant logDate;
    @Column(name = "NOTES",nullable = true,length = 150)
    private String notes;
    @Column(name = "COMPLETED",nullable = false)
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "habitId",nullable = false)
    private Habit habit;

}
