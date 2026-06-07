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

## Prerequisites

- PostgreSQL running locally; create db `devmock_db`
- Backend `.env` in `backend/` (copy `.env.example` if missing) — loaded by spring-dotenv
- Frontend `.env` in `frontend/` — `VITE_API_URL=http://localhost:8080`
- Schema: `ddl-auto=update` — no manual migrations

## Architecture

### Backend (`backend/`)
Standard layered layout under `com.devmock.backend`:
```
entity/en_enum/ → 16 JPA entities + enums
repository/     → 15 Spring Data JPA repos
service/        → 15 service interfaces
service/impl/   → 15 @Transactional implementations
controller/     → 16 REST controllers
dto/            → 48 request/response DTOs
security/       → JWT + Spring Security config (JwtAuthFilter, JwtService, SecurityConfig)
exception/      → GlobalExceptionHandler + domain exceptions
config/         → (empty dir — config is in security/)
```

### Frontend (`frontend/`)
```
src/api/          → 15 domain API modules (auth, users, questions, etc.)
                   Each domain has 3 files: {domain}.ts (fetch calls), {domain}.keys.ts (query keys), {domain}.queries.ts (React Query hooks)
src/components/   → ui/ (shadcn: button, card, dialog, input, label, badge), demo/, PrivateRoute, SidebarMenu
src/pages/        → LandingPage, LoginPage, RegisterPage, Dashboard, Demo
src/context/      → AuthContext (token + username in localStorage)
src/hooks/        → useDebouncedValue, useDeferredValue
src/layouts/      → MainLayout
src/lib/          → utils.ts (cn() helper)
```

## Code conventions (non-obvious)

### Backend
- **Constructor injection** only — no `@Autowired`
- **UUID PKs** — `GenerationType.UUID` on all entities except `SessionQuestion` (uses `@GeneratedValue`)
- **Soft delete** — `deletedAt` on User + Question only; others use hard `deleteById()`
- **Timestamps** — `Instant`, UTC, via `@PrePersist`/`@PreUpdate`
- **Lombok** declared but **not used on entities** — manual getters/setters (exists only in pom.xml)
- **`open-in-view=false`** — all DB access inside `@Transactional`
- **Entity relationships are commented out** — no `@ManyToOne`/`@OneToMany` wired yet
- **Score fields** — `BigDecimal` across 4 dimensions: correctness, efficiency, logic, clarity

### Frontend
- **API file pattern**: `{domain}.ts` (fetch functions via `http.ts`) → `{domain}.keys.ts` (query key factories via `createKeys()`) → `{domain}.queries.ts` (React Query hooks exporting `use*` functions)
- **`http.ts`**: Custom fetch wrapper; auto-injects `Authorization: Bearer <token>`, auto-redirects to `/login` on 401, exports `http<T>` (optional 204) and `httpRequired<T>` (asserts body)
- **`AuthContext`**: Token + username persisted to localStorage; `useAuth()` hook for login/logout/user state
- **Rendering**: `index.html` lang is `es` (Spanish); fonts: JetBrains Mono, Manrope, Work Sans

## Security

### Backend
- JWT stateless; BCrypt via `spring-security-crypto`
- Public: `POST /api/auth/**`
- Also public: `/api/interview-sessions/**`, `/api/session-questions/**`, `/api/user-performances/**`, `/api/rankings/**`, `/api/achievements/**`, `/api/user-achievements/**`, `/api/audit-logs/**`
- Everything else (`/api/users`, `/api/categories`, `/api/questions`, etc.) requires `Authorization: Bearer <token>`
- CORS: `http://localhost:5173` only
- **Important**: The public endpoints above are intentionally open per `SecurityConfig` — not a mistake

### Frontend
- Token stored in `localStorage` key `"token"`; username stored in `"username"`
- No refresh token logic implemented yet
- `PrivateRoute` component wraps pages that need auth (not yet used in router — currently all routes public)

## Tests

- Only 1 backend test: `BackendApplicationTests` — basic context load (`@SpringBootTest`)
- No frontend tests configured
- No integration test fixtures, testcontainers, or test DB config yet

## Git workflow

- Active branch: `Develop` — all PRs target it (not `main`)
- Conventional Commits: `feat(scope):`, `fix(scope):`, etc.
- Rebase feature branches; squash-and-merge preferred
- PR checklist: `mvnw.cmd clean package -DskipTests` then `mvnw.cmd test`

## Existing docs

- `CLAUDE.md` — outdated (says layers "to be added" but they exist); enums and commands still accurate
- `DEVELOP.md` — comprehensive Spanish Git/commit workflow guide (accurate)
- `frontend/GUIDE.md` — Tailwind v4 + shadcn/ui architecture guide for frontend devs
- `frontend/README.md` — generic Vite scaffold README (low value)
