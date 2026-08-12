# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

river-otter is a Spring Boot service that manages holidays and business days per country. It supports Korean and US public holidays, custom per-topic holiday calendars, business-day (working day) calculation, and solar/lunar calendar conversion for Korea.

## Commands

Build requires Java 21. `./gradlew build`/`test` run against an in-memory H2 DB — no Docker needed. Docker is only required for `./quick-start.sh` (runs the app against real MySQL).

```sh
./gradlew build              # compile, lint, and run full test suite (H2, no Docker)
./gradlew test               # run tests only
./gradlew test --tests "io.mustelidae.riverotter.domain.topic.TopicTest"   # single test class
./gradlew lintKotlin          # ktlint check (kotlinter plugin)
./gradlew formatKotlin        # auto-format Kotlin sources
./quick-start.sh             # docker-compose up (mysql) + build (skip tests) + run jar on :6400
```

Local run: `docker-compose up` starts MySQL on host port 3307 (container 3306, db `riverOtter`), app listens on `:6400`, Swagger UI at `/swagger-ui.html`.

## Architecture

### Domain module layout

Each domain lives under `domain/<name>/` and follows a consistent internal split:

- **Entity** (e.g. `Holiday.kt`, `Topic.kt`) — JPA entity plus domain behavior (e.g. `HolidayCalendar.addBy()/removeBy()`).
- **`XFinder`** — read-only query service (`@Service`), throws `DataNotFoundException` via a `findOrThrow` convention when a lookup is required.
- **`XInteraction`** — write/orchestration service (`@Service`, `@Transactional` per method), composes Finders + repositories to perform a use case.
- **`api/`** — `@RestController`s plus `XResources` classes holding nested `Request`/`Reply` DTOs for that controller.
- **`repository/`** — Spring Data JPA repositories.

Example: `domain/calendar/holiday/HolidayCalendarFinder.kt` (read) vs `domain/calendar/HolidayCalendarInteraction.kt` (write, transactional, depends on the Finder rather than duplicating lookups).

Domains present: `calendar` (holiday calendars per country/year, `PlanetCalendar` for solar/lunar conversion), `topic` (custom holiday calendars scoped to a "topic", with `synchronization/` classes that sync a topic's calendar against the base country calendar), `workingday` (business-day arithmetic via `WorkingDayCalculator`).

### External clients (`domain/client/`)

Each external integration (Korean government open-data API, AbstractAPI world holidays) has: a `Client` interface, a `StableClient` (real HTTP via Fuel), and a `DummyClient` (canned data, no network). `ClientDIConfiguration` picks Stable vs Dummy per client based on `AppEnvironment.client.*.useDummy`, driven by `app.client.government.use-dummy` / `app.client.abstract.use-dummy` in config. Both env profiles (`application-default.yml`, test `application.yml`) default these to dummy — real credentials must be swapped in to hit the live APIs (see README "Note" section for where to get keys).

### Cross-cutting (`common/`, `config/`)

- `Reply<T>` (`common/Reply.kt`) is the standard response envelope; `content` is `@JsonUnwrapped` so it flattens into the JSON body.
- `Error`/`ErrorCode` (`common/`) carry a code + message + optional `causeBy` map.
- Exception hierarchy (`config/GlobalExceptions.kt`): `HumanException` (client-caused, e.g. `DataNotFoundException`, `DuplicateDataException`) → HTTP 400; `CommunicationException` (upstream API failure) and `SystemException` (internal) → HTTP 500. `RestAPIExceptionAdvice` (`@ControllerAdvice`) maps these to a uniform `GlobalErrorFormat` JSON error body.
- `AppEnvironment` (`config/AppEnvironment.kt`) is the `@ConfigurationProperties(prefix = "app")` root for all app-specific config (available countries, per-country rules like `saturdayIsHoliday`, client settings).

### Persistence

MySQL via Spring Data JPA + Flyway (`src/main/resources/db/migration/V*__*.sql`); `ddl-auto: validate` — schema changes go through a new Flyway migration, not entity annotations alone. This applies to the real (prod/local) datasource only — the test profile bypasses Flyway entirely and lets Hibernate generate the schema from entities (see Testing conventions below), so migration SQL is MySQL-specific and untested by `./gradlew test`. The project was migrated from MongoDB to MySQL/JPA (see git history) — don't reintroduce Mongo-style document assumptions (e.g. string/ObjectId-style IDs) in new code without checking the current entity definitions.

### Testing conventions

- `IntegrationSupport` — base for `@SpringBootTest` tests that don't need MockMvc.
- `FlowTestSupport` — base for `@SpringBootTest` + `@AutoConfigureMockMvc` end-to-end tests.
- Both run against an in-memory H2 database (base settings in `src/test/resources/application.yml`: `MODE=MySQL` compatibility, `spring.jpa.hibernate.ddl-auto: create`, `spring.flyway.enabled: false` — Hibernate generates the schema straight from the JPA entities instead of running the production Flyway migrations, which keeps the suite fast and Docker-free). `NON_KEYWORDS=USER,YEAR` is required in the JDBC URL because H2's MySQL mode reserves `YEAR`, which collides with the `year` column on `HolidayCalendar`/`TopicCalendar`.
- `IntegrationSupport` and `FlowTestSupport` each override `spring.datasource.url` (via `@TestPropertySource`) to point at their **own** uniquely-named H2 database (`jdbc:h2:mem:integration` / `jdbc:h2:mem:flow`) rather than sharing one. Don't collapse these back to a shared URL — Spring caches one `ApplicationContext` per distinct test config, so the two groups are backed by separate database instances; sharing a URL lets one group's test mutate data (e.g. `HolidayCalendarMaintenanceControllerTest` deleting a holiday) out from under the other.
- `XControllerFlow` classes (e.g. `TopicControllerFlow.kt`) wrap `MockMvc` calls per controller, building request URIs via Spring HATEOAS `linkTo<Controller> { method(...) }` rather than hardcoded paths, and deserialize `Reply<T>` responses. `XFlowTest`/`XControllerFlowTest` classes compose these Flow helpers into end-to-end scenarios across multiple endpoints.
- Plain `XControllerTest`/unit test classes test a single class in isolation (mockk for mocking, kotest assertions available).
- No mocked persistence in flow/integration tests — they read/write against the real H2 instance.

## Claude Code Instructions

### CLI Tools

Use modern CLI replacements instead of the legacy tools:

```sh
rg 'pattern'                # instead of grep
fd 'pattern'                # instead of find
bat src/main/.../Foo.kt     # instead of cat
eza --tree src/main         # instead of ls -R
jq '.dependencies' < f.json
sd 'old' 'new' file.kt      # instead of sed
```

### Code Style

- New entities extend `Audit` and apply `@Audited(targetAuditMode = NOT_AUDITED)`.
- `*Resources.kt` inner-class conventions, keyed off HTTP method:
  - `POST` → DTO nested under `Request.Create`.
  - `PUT`, `PATCH` → DTO nested under `Modify`.
  - `DELETE` → DTO nested under `Remove`.
  - Response DTOs always nest under `Reply`, regardless of HTTP method.
  - `Request`, `Modify`, `Remove`, `Reply` are pure namespaces — never DTOs themselves.
- Business logic belongs in `*Interaction`; `*Finder` stays read-only.
- Association convenience methods follow `addBy(entity)` / `setBy(entity)` naming.
- Soft delete via a `status = true/false` column plus `@SQLRestriction("status = true")`.
- Every DTO inner class in `*Resources.kt` must carry `@Schema(name = "...")`, named `Lutrogale.{Domain}.{InnerClassPath}`:
  - `{Domain}` = the `*` in `*Resources.kt` (e.g. `User`, `Admin`, `Access`).
  - `{InnerClassPath}` = the dotted inner-class path (e.g. `Request.Create`, `Modify.Info`).
  - e.g. `Lutrogale.User.Request.Create`, `Lutrogale.User.Request.BatchRegister`, `Lutrogale.User.Reply.Simple`, `Lutrogale.User.Modify.Info`.

### Testing Rules

- TDD for every new feature/bugfix.
- Integration tests extend `FlowTestSupport` combined with `*Flow` helpers.
- Use the H2 test DB — tests must run without a real MySQL connection.
- No mocked DB. Write tests that read/write against the real H2 instance.

