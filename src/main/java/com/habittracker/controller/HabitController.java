package com.habittracker.controller;

import com.habittracker.generated.model.CompletionRequest;
import com.habittracker.generated.model.HabitRequest;
import com.habittracker.generated.model.HabitResponse;
import com.habittracker.generated.model.PagedHabitResponse;
import com.habittracker.generated.model.ProgressPeriod;
import com.habittracker.generated.model.ProgressResponse;
import com.habittracker.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Implements the "Habits" paths defined in the OpenAPI contract
 * (src/main/resources/openapi/habit-tracker-api.yaml), using the
 * request/response DTOs generated from that contract.
 */
@RestController
@RequestMapping("/api/habits")
@Tag(name = "Habits", description = "Habit CRUD, completion tracking, progress")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @GetMapping
    @Operation(operationId = "getHabits", summary = "List habits for the current user")
    public ResponseEntity<PagedHabitResponse> getHabits(
            Authentication authentication,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) com.habittracker.generated.model.HabitCategory category,
            @RequestParam(required = false) Boolean completedToday,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        com.habittracker.domain.HabitCategory domainCategory =
                category != null ? com.habittracker.domain.HabitCategory.valueOf(category.name()) : null;

        PagedHabitResponse response = habitService.getHabits(
                authentication.getName(), name, domainCategory, completedToday, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(operationId = "createHabit", summary = "Create a new habit")
    public ResponseEntity<HabitResponse> createHabit(Authentication authentication,
                                                       @Valid @RequestBody HabitRequest habitRequest) {
        HabitResponse response = habitService.createHabit(authentication.getName(), habitRequest);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{habitId}")
    @Operation(operationId = "getHabitById", summary = "Get a single habit by id")
    public ResponseEntity<HabitResponse> getHabitById(Authentication authentication, @PathVariable Long habitId) {
        HabitResponse response = habitService.getHabitById(authentication.getName(), habitId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{habitId}")
    @Operation(operationId = "updateHabit", summary = "Update an existing habit")
    public ResponseEntity<HabitResponse> updateHabit(Authentication authentication,
                                                       @PathVariable Long habitId,
                                                       @Valid @RequestBody HabitRequest habitRequest) {
        HabitResponse response = habitService.updateHabit(authentication.getName(), habitId, habitRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{habitId}")
    @Operation(operationId = "deleteHabit", summary = "Delete a habit")
    public ResponseEntity<Void> deleteHabit(Authentication authentication, @PathVariable Long habitId) {
        habitService.deleteHabit(authentication.getName(), habitId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{habitId}/complete")
    @Operation(operationId = "completeHabit", summary = "Mark a habit as completed for a given day")
    public ResponseEntity<HabitResponse> completeHabit(
            Authentication authentication,
            @PathVariable Long habitId,
            @RequestBody(required = false) CompletionRequest completionRequest) {
        HabitResponse response = habitService.completeHabit(authentication.getName(), habitId, completionRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{habitId}/progress")
    @Operation(operationId = "getHabitProgress", summary = "View progress for a habit over a period")
    public ResponseEntity<ProgressResponse> getHabitProgress(
            Authentication authentication,
            @PathVariable Long habitId,
            @RequestParam(required = false, defaultValue = "WEEKLY") ProgressPeriod period) {
        ProgressResponse response = habitService.getProgress(authentication.getName(), habitId, period);
        return ResponseEntity.ok(response);
    }
}
