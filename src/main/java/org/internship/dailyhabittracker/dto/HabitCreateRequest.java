package org.internship.dailyhabittracker.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payload for POST /habits (US-004). Name is required; category is optional.
 * Validation ranges match US-014.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HabitCreateRequest {

    @NotBlank(message = "Habit name cannot be blank")
    @Size(min = 3, max = 100, message = "Habit name must be between 3 and 100 characters")
    private String name;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;
}