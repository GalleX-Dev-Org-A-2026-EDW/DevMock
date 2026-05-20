# AGENTS.md — DevMock

Single-module Maven project (Spring Boot 4.0.6 / Java 25). No frontend. No Docker. No CI.

## Commands (run from `backend/`)

```bash
mvnw.cmd spring-boot:run           # dev server with devtools hot-reload on :8080
mvnw.cmd clean package -DskipTests # build (no tests)
mvnw.cmd test                      # all tests
mvnw.cmd test -Dtest=Class#method  # single test method
```

## Prerequisites

- PostgreSQL running locally; create db `devmock_db`
- `.env` in `backend/` loaded by spring-dotenv — copy `.env.example` if missing
- Schema: `ddl-auto=update` — no manual migrations

## Architecture

Standard layered layout under `com.devmock.backend`:

```
entity/en_enum/ → JPA entities + enums
repository/     → Spring Data JPA repos
service/impl/   → @Transactional business logic
controller/     → REST controllers
dto/            → request/response DTOs
security/       → JWT + Spring Security config
exception/      → GlobalExceptionHandler
```

## Code conventions (non-obvious)

- **Constructor injection** only — no `@Autowired`
- **UUID PKs** — `GenerationType.UUID` on all entities
- **Soft delete** — `deletedAt` on User only; others use hard `deleteById()` (inconsistent)
- **Timestamps** — `Instant`, UTC, via `@PrePersist`/`@PreUpdate`
- **Lombok** declared but **not used on entities** — manual getters/setters
- **`open-in-view=false`** — all DB access inside `@Transactional`

## Domain model quirks

- **Entity relationships are commented out** — no `@ManyToOne`/`@OneToMany` wired yet (foreign keys will be added later)
- **`SessionQuestion`** uses plain `@GeneratedValue` instead of `GenerationType.UUID` — outlier
- **Score fields** — `BigDecimal` across 4 dimensions: correctness, efficiency, logic, clarity
- **DifficultyLevel** — `pointsMultiplier` decimal(4,2), unique `levelOrder`
- **Category** — `slug` is unique, `displayOrder` for sorting

## Security

- JWT stateless; BCrypt via `spring-security-crypto`
- Public: `POST /api/auth/**`
- Everything else: `Authorization: Bearer <token>`
- CORS: `http://localhost:5173` only

## Tests

- Only 1 test exists: `BackendApplicationTests` — basic context load (`@SpringBootTest`)
- No integration test fixtures, testcontainers, or test DB config yet

## Git workflow

- Active branch: `Develop` — all PRs target it (not `main`)
- Conventional Commits: `feat(scope):`, `fix(scope):`, etc.
- Rebase feature branches; squash-and-merge preferred
- PR checklist: `mvnw.cmd clean package -DskipTests` then `mvnw.cmd test`

## Existing docs to use/ignore

- `CLAUDE.md` — outdated architecture diagram (says layers are "to be added" but they exist); enums and commands are still accurate
- `DEVELOP.md` — comprehensive Spanish Git/commit workflow guide
