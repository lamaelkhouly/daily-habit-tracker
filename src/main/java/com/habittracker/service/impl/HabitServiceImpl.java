package com.habittracker.service.impl;

import com.habittracker.domain.Habit;
import com.habittracker.domain.HabitCompletion;
import com.habittracker.domain.User;
import com.habittracker.exception.HabitNotFoundException;
import com.habittracker.exception.UserNotFoundException;
import com.habittracker.generated.model.CompletionRequest;
import com.habittracker.generated.model.HabitRequest;
import com.habittracker.generated.model.HabitResponse;
import com.habittracker.generated.model.PagedHabitResponse;
import com.habittracker.generated.model.ProgressPeriod;
import com.habittracker.generated.model.ProgressResponse;
import com.habittracker.mapper.HabitMapper;
import com.habittracker.repository.HabitCompletionRepository;
import com.habittracker.repository.HabitRepository;
import com.habittracker.repository.UserRepository;
import com.habittracker.service.HabitService;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final HabitCompletionRepository completionRepository;
    private final UserRepository userRepository;
    private final HabitMapper habitMapper;

    public HabitServiceImpl(HabitRepository habitRepository,
                             HabitCompletionRepository completionRepository,
                             UserRepository userRepository,
                             HabitMapper habitMapper) {
        this.habitRepository = habitRepository;
        this.completionRepository = completionRepository;
        this.userRepository = userRepository;
        this.habitMapper = habitMapper;
    }

    @Override
    public PagedHabitResponse getHabits(String username, String name, com.habittracker.domain.HabitCategory category,
                                         Boolean completedToday, int page, int size, String sort) {
        User user = getUser(username);

        Specification<Habit> spec = buildSpecification(user.getId(), name, category, completedToday);
        Page<Habit> habitPage = habitRepository.findAll(spec, buildPageable(page, size, sort));

        List<HabitResponse> content = new ArrayList<>();
        for (Habit habit : habitPage.getContent()) {
            content.add(enrich(habit));
        }

        PagedHabitResponse response = new PagedHabitResponse();
        response.setContent(content);
        response.setPage(habitPage.getNumber());
        response.setSize(habitPage.getSize());
        response.setTotalElements(habitPage.getTotalElements());
        response.setTotalPages(habitPage.getTotalPages());
        return response;
    }

    @Override
    @Transactional
    public HabitResponse createHabit(String username, HabitRequest request) {
        User user = getUser(username);

        Habit habit = Habit.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(com.habittracker.domain.HabitCategory.valueOf(request.getCategory().name()))
                .user(user)
                .build();

        habitRepository.save(habit);
        return enrich(habit);
    }

    @Override
    public HabitResponse getHabitById(String username, Long habitId) {
        Habit habit = getOwnedHabit(username, habitId);
        return enrich(habit);
    }

    @Override
    @Transactional
    public HabitResponse updateHabit(String username, Long habitId, HabitRequest request) {
        Habit habit = getOwnedHabit(username, habitId);

        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setCategory(com.habittracker.domain.HabitCategory.valueOf(request.getCategory().name()));

        habitRepository.save(habit);
        return enrich(habit);
    }

    @Override
    @Transactional
    public void deleteHabit(String username, Long habitId) {
        Habit habit = getOwnedHabit(username, habitId);
        habitRepository.delete(habit);
    }

    @Override
    @Transactional
    public HabitResponse completeHabit(String username, Long habitId, CompletionRequest request) {
        Habit habit = getOwnedHabit(username, habitId);

        LocalDate date = (request != null && request.getDate() != null) ? request.getDate() : LocalDate.now();

        if (!completionRepository.existsByHabitIdAndCompletedDate(habitId, date)) {
            HabitCompletion completion = HabitCompletion.builder()
                    .habit(habit)
                    .completedDate(date)
                    .build();
            completionRepository.save(completion);
        }

        return enrich(habit);
    }

    @Override
    public ProgressResponse getProgress(String username, Long habitId, ProgressPeriod period) {
        getOwnedHabit(username, habitId); // ensures ownership + existence

        ProgressPeriod effectivePeriod = period != null ? period : ProgressPeriod.WEEKLY;
        LocalDate today = LocalDate.now();
        int expectedDays = switch (effectivePeriod) {
            case DAILY -> 1;
            case WEEKLY -> 7;
            case MONTHLY -> 30;
        };
        LocalDate start = today.minusDays(expectedDays - 1L);

        List<HabitCompletion> completions =
                completionRepository.findByHabitIdAndCompletedDateBetweenOrderByCompletedDateDesc(habitId, start, today);

        List<LocalDate> completedDates = completions.stream()
                .map(HabitCompletion::getCompletedDate)
                .toList();

        ProgressResponse response = new ProgressResponse();
        response.setHabitId(habitId);
        response.setPeriod(effectivePeriod);
        response.setTotalCompletions(completedDates.size());
        response.setExpectedDays(expectedDays);
        response.setCompletionRate(expectedDays == 0 ? 0.0 : (double) completedDates.size() / expectedDays);
        response.setCompletedDates(completedDates);
        return response;
    }

    // ---- helpers ----

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    private Habit getOwnedHabit(String username, Long habitId) {
        User user = getUser(username);
        return habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new HabitNotFoundException(habitId));
    }

    private HabitResponse enrich(Habit habit) {
        LocalDate today = LocalDate.now();
        boolean completedToday = completionRepository.existsByHabitIdAndCompletedDate(habit.getId(), today);
        int streak = computeStreak(habit.getId());
        return habitMapper.toResponse(habit, completedToday, streak);
    }

    private int computeStreak(Long habitId) {
        Set<LocalDate> completedDates = new LinkedHashSet<>();
        completionRepository.findByHabitIdOrderByCompletedDateDesc(habitId)
                .forEach(c -> completedDates.add(c.getCompletedDate()));

        int streak = 0;
        LocalDate cursor = LocalDate.now();
        while (completedDates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private Specification<Habit> buildSpecification(Long userId, String name,
                                                      com.habittracker.domain.HabitCategory category,
                                                      Boolean completedToday) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (category != null) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            if (completedToday != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                var completionRoot = subquery.from(HabitCompletion.class);
                subquery.select(completionRoot.get("id"));
                subquery.where(
                        cb.equal(completionRoot.get("habit"), root),
                        cb.equal(completionRoot.get("completedDate"), LocalDate.now())
                );
                predicates.add(completedToday ? cb.exists(subquery) : cb.not(cb.exists(subquery)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Pageable buildPageable(int page, int size, String sort) {
        Sort sortObj = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            Sort.Direction direction = (parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc"))
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sortObj = Sort.by(direction, field);
        }
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), sortObj);
    }
}
