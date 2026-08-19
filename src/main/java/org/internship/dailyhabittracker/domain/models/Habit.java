package org.internship.dailyhabittracker.domain.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a habit that a user wants to track daily.
 * See US-004 (Create), US-005 (View), US-006 (Update), US-007 (Delete),
 * US-011/US-012 (Search & Filter), US-014 (Validation).
 */
@Entity
@Table(name = "habits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Habit name cannot be blank")
    @Size(min = 3, max = 100, message = "Habit name must be between 3 and 100 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Every habit belongs to exactly one user (US-004, US-005).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Deleting a habit removes all its completion records (US-007).
    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HabitCompletion> completions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}