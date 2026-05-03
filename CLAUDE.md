# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DevMock is a mock interview / technical practice platform. Users (students or professionals) complete timed interview sessions with questions, receive feedback, and compete on leaderboards.

## Backend Commands

All commands run from the `backend/` directory.

```bash
# Run the application
./mvnw spring-boot:run

# Build (skip tests)
./mvnw clean package -DskipTests

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=BackendApplicationTests

# Run a single test method
./mvnw test -Dtest=ClassName#methodName
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

## Tech Stack

- **Java 25** / **Spring Boot 4.0.6**
- **Spring Data JPA** + **Hibernate** — `ddl-auto=update` (schema auto-managed in dev)
- **PostgreSQL** — `localhost:5432/devmock_db`, user `postgres`, password `12345678`
- **Lombok** — used for boilerplate reduction (getters, builders, etc.)
- **Spring Validation** — Bean Validation on request bodies

## Architecture

The backend follows a standard layered Spring Boot structure:

```
com.devmock.backend
├── entity/           # JPA entities
│   └── en_enum/      # Domain enumerations
├── repository/       # Spring Data JPA repositories (to be added)
├── service/          # Business logic (to be added)
├── controller/       # REST controllers (to be added)
└── dto/              # Request/response DTOs (to be added)
```

### Domain Model (inferred from enums)

| Enum | Values |
|------|--------|
| `UserRole` | `STUDENT`, `PROFESSIONAL`, `ADMIN` |
| `QuestionType` | `THEORETICAL`, `PRACTICAL`, `MIXED` |
| `AnswerFormat` | `FREE_TEXT`, `CODE`, `MULTIPLE_CHOICE`, `SINGLE_CHOICE` |
| `SessionStatus` | `IN_PROGRESS`, `COMPLETED`, `ABANDONED`, `EXPIRED` |
| `RankingPeriod` | `WEEKLY`, `MONTHLY`, `ALL_TIME` |
| `AuditAction` | `CREATE`, `UPDATE`, `DELETE`, `LOGIN`, `LOGOUT` |

## Database

PostgreSQL must be running locally before starting the app. The schema is managed automatically by Hibernate (`ddl-auto=update`). SQL queries are logged to the console (`show-sql=true`).

## Branch Strategy

- `main` — stable/production
- `develop` (or `Develop`) — active development branch; open PRs against this branch
