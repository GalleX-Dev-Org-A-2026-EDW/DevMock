# AGENTS.md — DevMock

Full-stack monorepo: Spring Boot 4.0.6 / Java 25 backend + Vite 8 / React 19 / TypeScript 6 frontend. No Docker. No CI.

## Commands

### Backend (run from `backend/`)
```bash
mvnw.cmd spring-boot:run           # dev server with devtools hot-reload on :8080
mvnw.cmd clean package -DskipTests # build (no tests)
mvnw.cmd test                      # all tests
mvnw.cmd test -Dtest=Class#method  # single test method
```

### Frontend (run from `frontend/`)
```bash
npm run dev      # Vite dev server on :5173
npm run build    # tsc -b && vite build
npm run lint     # ESLint
npm run preview  # preview production build
```

### Verification order (PR checklist)
```bash
cd backend   ; mvnw.cmd clean package -DskipTests  # compile check
cd backend   ; mvnw.cmd test                       # all tests pass
cd frontend  ; npm run lint                         # lint
cd frontend  ; npm run build                        # typecheck + build
```

## Prerequisites

- PostgreSQL running locally; create db `devmock_db` (user `postgres` / password `1234` in `application.properties`)
- `.env` files: `backend/.env` (loaded by spring-dotenv) — copy from `.env.example`; `frontend/.env` must have `VITE_API_URL=http://localhost:8080`
- Schema: `ddl-auto=update` — no manual migrations
- Seed data: `DataInitializer.java` runs on startup if DB is empty (creates admin user `admin@devmock.com` / `Admin123!`, difficulty levels, categories, interview types, evaluation criteria, achievements, sample questions)

## Architecture

### Backend (`backend/src/main/java/com/devmock/backend/`)
```
entity/       → 13 JPA entities (UUID PKs via GenerationType.UUID)
en_enum/      → 5 domain enums (UserRole, QuestionType, AnswerFormat, SessionStatus, RankingPeriod, AuditAction)
repository/   → 13 Spring Data JPA repos
service/      → 13 service interfaces
service/impl/ → 13 @Transactional implementations
controller/   → 16 REST controllers (includes AuthController + AdminDashboardController)
dto/          → ~50 request/response DTOs
security/     → JWT + Spring Security (JwtAuthFilter, JwtService, SecurityConfig, UserDetailsServiceImpl)
config/       → DataInitializer (seed data CommandLineRunner)
exception/    → GlobalExceptionHandler + domain exceptions
```

### Frontend (`frontend/src/`)
```
api/         → 15 domain API modules, each: {domain}.ts → {domain}.keys.ts → {domain}.queries.ts
               Also: http.ts (fetch wrapper), auth.ts (separate fetch for login/register), QueryProvider.tsx
components/  → ui/ (shadcn: button, card, dialog, input, label, badge), PrivateRoute, AdminRoute, AdminSidebar
               SidebarMenu, InterviewView, CreateSessionView, SessionResultsView, demo/
pages/       → LandingPage, LoginPage, RegisterPage, Dashboard, Demo
               admin/ (AdminDashboard, AdminUsers, AdminQuestions, AdminCategories, AdminAchievements,
                       AdminDifficultyLevels, AdminInterviewTypes, AdminEvaluationCriteria, AdminAuditLogs)
context/     → AuthContext (token + username + role in localStorage)
hooks/       → useDebouncedValue, useDeferredValue, types.ts
layouts/     → MainLayout
lib/         → utils.ts (cn() helper)
```

## Security

### Backend (verified in `SecurityConfig.java`)
- JWT stateless; `BCryptPasswordEncoder`
- **Only public**: `POST /api/auth/**` (login, register)
- **All other endpoints** (`/api/users`, `/api/categories`, `/api/questions`, `/api/interview-sessions`, etc.) require `Authorization: Bearer <token>`
- CORS: `http://localhost:5173` only, credentials allowed
- `open-in-view=false` — all DB access inside `@Transactional`

### Frontend
- Token, username, and **role** stored in `localStorage` keys `"token"`, `"username"`, `"role"`
- `http.ts`: auto-injects `Authorization` header; clears auth and redirects to `/login` on 401
- `PrivateRoute`: renders children if user is authenticated, else `fallback`
- `AdminRoute`: renders children only if `user.role === "ADMIN"`, else `fallback`
- No refresh token logic

## Code conventions (non-obvious)

### Backend
- **Constructor injection** only — no `@Autowired`
- **UUID PKs** via `GenerationType.UUID` on all entities; `SessionQuestion` uses `@GeneratedValue` (auto-increment)
- **Soft delete** — `deletedAt` on `User` + `Question` only; others use hard `deleteById()`
- **Timestamps** — `Instant`, UTC, via `@PrePersist`/`@PreUpdate`
- **Lombok** in pom.xml but **not used on entities** — manual getters/setters
- **Entity relationships ARE wired** — `@ManyToOne`/`@OneToMany` active (User ↔ InterviewSession, Category → parent/children, etc.)
- **Evaluation** uses 4 criteria: Correctness (40%), Efficiency (25%), Clarity (20%), Logic (15%) — stored as `EvaluationCriterion` rows, seeded by `DataInitializer`
- **Score fields** on `SessionQuestion`: `BigDecimal` for correctnessScore, efficiencyScore, logicScore, clarityScore
- **Slugs** on Category, DifficultyLevel, InterviewType, Achievement, EvaluationCriterion — unique, lowercase+hyphens

### Frontend
- **API file pattern**: `{domain}.ts` (fetch via `http<T>` / `httpRequired<T>` ) → `{domain}.keys.ts` (query key factories via `createKeys()`) → `{domain}.queries.ts` (React Query hooks exporting `use*` functions)
- **Auth API** (`auth.ts`) is separate from the `http.ts` pattern — uses direct `fetch` and sets token/role in localStorage on success
- **`http.ts`**: `http<T>` returns `T | undefined` (handles 204); `httpRequired<T>` asserts body exists
- **index.html**: lang `es` (Spanish); fonts: JetBrains Mono, Manrope, Work Sans
- **`@/` path alias** configured in `vite.config.ts` for imports
- **React Query** in `main.tsx`: `staleTime: 5min`, `retry: 1`, `refetchOnWindowFocus: false`

## Tests

- Only 1 backend test: `BackendApplicationTests` — `@SpringBootTest` context load
- No frontend tests
- No testcontainers, no test DB config

## Existing docs (keep referenced)

- `CLAUDE.md` — outdated architecture claims but enum values and DB creds still accurate
- `DEVELOP.md` — Spanish Git/commit workflow guide (accurate)
- `frontend/GUIDE.md` — Tailwind v4 + shadcn/ui setup and patterns

## Git workflow

- Active branch: `Develop` — all PRs target it (not `main`)
- Conventional Commits: `feat(scope):`, `fix(scope):`, etc.
- Rebase feature branches; squash-and-merge preferred
- PR checklist: build → test → lint
