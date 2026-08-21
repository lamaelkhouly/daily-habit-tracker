# Daily Habit Tracker API

Contract-first Spring Boot 3 / Java 17 REST API for tracking daily habits.

## How "contract-first" works here

The single source of truth is:

```
src/main/resources/openapi/habit-tracker-api.yaml
```

On every `mvn compile`, the `openapi-generator-maven-plugin` reads that YAML and
generates, into `target/generated-sources/openapi`:

- **DTOs** — `com.habittracker.generated.model.*` (RegisterRequest, LoginRequest,
  AuthResponse, HabitRequest, HabitResponse, PagedHabitResponse, CompletionRequest,
  ProgressResponse, ErrorResponse, HabitCategory, ProgressPeriod)
- **API interfaces** — `com.habittracker.generated.api.*` (AuthApi, HabitsApi)

Hand-written code (entities, repositories, services, security, exception handling,
controllers, seeder) lives under `src/main/java/com/habittracker` and depends only
on those generated DTOs — never redefining request/response shapes by hand.

> **Note on controllers:** the generated `AuthApi`/`HabitsApi` interfaces are
> available in `target/generated-sources` for reference, but `AuthController` and
> `HabitController` are written directly against the contract's paths/methods
> rather than `implements AuthApi` — this sandbox has no access to Maven Central,
> so the exact generated interface signatures couldn't be verified end-to-end
> before delivery. The controllers still route the exact paths/verbs from the
> YAML and use only the generated DTOs, so the contract remains the source of
> truth. If you want strict `implements AuthApi` controllers, run `mvn compile`
> locally, open the generated interface, and have your IDE "implement methods" —
> it's a couple minutes of work with real generated code in front of you.

## Tech stack

- Spring Boot 3.3, Spring Data JPA, H2 (in-memory)
- Spring Security with stateless JWT auth (jjwt 0.12)
- springdoc-openapi (Swagger UI)
- Lombok
- openapi-generator-maven-plugin (contract-first codegen)
- JUnit 5 / Mockito dependencies present (no tests implemented, per project brief)

## Running it

```bash
mvn spring-boot:run
```

- API base path: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:habittracker`, user `sa`, empty password)

On first startup, `DataSeeder` creates two demo accounts and some habit history:

| username | password | role  |
|----------|----------|-------|
| demo     | demo1234 | USER  |
| admin    | admin123 | ADMIN |

## Typical flow

```bash
# 1. Login (or register a new user)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo1234"}'
# => { "token": "...", "tokenType": "Bearer", "username": "demo", "role": "USER" }

# 2. Call a protected endpoint with the token
curl http://localhost:8080/api/habits?page=0&size=10 \
  -H "Authorization: Bearer <token>"

# 3. Create a habit
curl -X POST http://localhost:8080/api/habits \
  -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"name":"Drink water","description":"8 glasses a day","category":"HEALTH"}'

# 4. Mark it complete for today
curl -X POST http://localhost:8080/api/habits/1/complete \
  -H "Authorization: Bearer <token>"

# 5. Check progress
curl "http://localhost:8080/api/habits/1/progress?period=WEEKLY" \
  -H "Authorization: Bearer <token>"
```

## Endpoints (mirrors the OpenAPI contract exactly)

| Method | Path                          | Auth | Description                          |
|--------|-------------------------------|------|---------------------------------------|
| POST   | /api/auth/register             | no   | Register a new user                  |
| POST   | /api/auth/login                | no   | Login, returns JWT                   |
| GET    | /api/habits                    | yes  | List habits (search/filter/paginate) |
| POST   | /api/habits                    | yes  | Create habit                         |
| GET    | /api/habits/{habitId}          | yes  | Get habit by id                      |
| PUT    | /api/habits/{habitId}          | yes  | Update habit                         |
| DELETE | /api/habits/{habitId}          | yes  | Delete habit                         |
| POST   | /api/habits/{habitId}/complete | yes  | Mark completed for a day (default today) |
| GET    | /api/habits/{habitId}/progress | yes  | Progress summary (DAILY/WEEKLY/MONTHLY) |

All errors return the contract's `ErrorResponse` shape (`timestamp`, `status`, `error`, `message`, `path`),
produced by `GlobalExceptionHandler` (`@ControllerAdvice`).

## Project layout

```
src/main/java/com/habittracker/
  config/       SecurityConfig, OpenApiConfig
  security/     JwtUtil, JwtAuthenticationFilter, JwtAuthenticationEntryPoint, CustomUserDetailsService
  domain/       User, Habit, HabitCompletion, Role, HabitCategory
  repository/   UserRepository, HabitRepository (+ Specification support), HabitCompletionRepository
  service/      UserService, HabitService (interfaces) + impl/
  mapper/       HabitMapper
  controller/   AuthController, HabitController
  exception/    HabitNotFoundException, UserNotFoundException, UserAlreadyExistsException,
                InvalidCredentialsException, GlobalExceptionHandler
  seeder/       DataSeeder
src/main/resources/
  openapi/habit-tracker-api.yaml   <- the contract
  application.yml
```
