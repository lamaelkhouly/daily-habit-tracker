package com.habittracker.seeder;

import com.habittracker.domain.*;
import com.habittracker.repository.HabitCompletionRepository;
import com.habittracker.repository.HabitRepository;
import com.habittracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Seeds the in-memory H2 database with demo users, habits, and a few weeks of
 * completion history so the API is immediately explorable via Swagger UI.
 * Toggle with app.seed.enabled=false in application.yml.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitCompletionRepository completionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                       HabitRepository habitRepository,
                       HabitCompletionRepository completionRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
        this.completionRepository = completionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    private final Random random = new Random(42);

    private static final List<String> HABIT_NAMES = List.of(
            "Drink 8 glasses of water", "Read for 30 minutes", "Morning run",
            "Meditate", "Journal", "No sugar", "Stretch for 10 minutes",
            "Practice guitar", "Learn Spanish", "Sleep before 11pm"
    );

    @Override
    public void run(String... args) {
        if (!seedEnabled || userRepository.count() > 0) {
            log.info("Skipping data seed (already seeded or disabled)");
            return;
        }

        log.info("Seeding demo data...");

        User admin = createUser("admin", "admin@habittracker.com", "admin123", Role.ADMIN);
        User demoUser = createUser("demo", "demo@habittracker.com", "demo1234", Role.USER);

        seedHabitsFor(demoUser);
        seedHabitsFor(admin);

        log.info("Seed complete. Demo credentials -> username: demo / password: demo1234");
    }

    private User createUser(String username, String email, String rawPassword, Role role) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        return userRepository.save(user);
    }

    private void seedHabitsFor(User user) {
        HabitCategory[] categories = HabitCategory.values();

        for (int i = 0; i < 5; i++) {
            String name = HABIT_NAMES.get(random.nextInt(HABIT_NAMES.size()));
            Habit habit = Habit.builder()
                    .name(name)
                    .description("Auto-generated demo habit: " + name)
                    .category(categories[random.nextInt(categories.length)])
                    .user(user)
                    .build();
            habit = habitRepository.save(habit);

            // Randomly mark some of the last 14 days as completed to demonstrate streaks/progress.
            for (int daysAgo = 0; daysAgo < 14; daysAgo++) {
                if (random.nextBoolean()) {
                    LocalDate date = LocalDate.now().minusDays(daysAgo);
                    HabitCompletion completion = HabitCompletion.builder()
                            .habit(habit)
                            .completedDate(date)
                            .build();
                    completionRepository.save(completion);
                }
            }
        }
    }
}
