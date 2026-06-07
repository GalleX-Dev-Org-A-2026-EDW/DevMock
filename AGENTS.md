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
npm run lint     # ESLint (flat config, eslint.config.js)
npm run preview  # preview production build
```

## Prerequisites

- PostgreSQL running locally; db `devmock_db`, user `postgres`, pass `12345678`
- No backend `.env` file — DB creds hardcoded in `application.properties` (though `spring-dotenv` dep is present in pom.xml, so one *could* be added)
- Frontend `.env` at `frontend/` — `VITE_API_URL=http://localhost:8080` (note: no `/api` suffix; `http.ts` prepends it). `.env.example` is stale (wrong URL format).
- Schema: `ddl-auto=update` — no manual migrations; SQL logged to console
- `DataInitializer.java` auto-seeds on empty DB: admin user (`admin@devmock.com` / `Admin123!`), difficulty levels, categories, interview types, evaluation criteria, achievements, sample questions. **All seed data in Spanish** (e.g., "Fácil", "Algoritmos").

## Architecture

### Backend (`backend/`)
Layered layout under `com.devmock.backend` with 15 entities + 6 enums, **16 services** (interface + `@Transactional` impl), 17 controllers, 15 repos, 49 DTOs:
```
entity/         → 15 JPA entities
entity/en_enum/ → 6 enums (UserRole, QuestionType, AnswerFormat, SessionStatus, RankingPeriod, AuditAction)
repository/     → Spring Data JPA repos
service/        → service interfaces (16)
service/impl/   → @Transactional implementations
controller/     → REST controllers (17, incl. AuthController + AdminDashboardController)
dto/            → 49 request/response DTOs
security/       → JWT + Spring Security
exception/      → GlobalExceptionHandler + domain exceptions
config/         → DataInitializer.java (seed data CommandLineRunner)
```

### Frontend (`frontend/`)
```
src/api/          → 17 domain modules, each: {domain}.ts → {domain}.keys.ts → {domain}.queries.ts
                   Exception: auth has no .keys.ts
                   QueryProvider.tsx is DEAD CODE (main.tsx creates QueryClient directly)
src/components/   → ui/ (6 shadcn components: badge, button, card, dialog, input, label)
                   PrivateRoute.tsx, AdminRoute.tsx, AdminSidebar, InterviewView, etc.
src/pages/        → LandingPage, LoginPage, RegisterPage, Dashboard, Demo + admin/ (9 pages)
src/context/      → AuthContext (token, username, role in localStorage)
src/hooks/        → useDebouncedValue, useDeferredValue
src/layouts/      → MainLayout
src/lib/          → utils.ts (cn() via clsx + tailwind-merge)
```

### Routing (`App.tsx` via `createBrowserRouter`)
- Public: `/`, `/login`, `/register`
- Private (`PrivateRoute`): `/dashboard`
- Admin (`AdminRoute` + `AdminLayout`): `/admin`, `/admin/users`, `/admin/questions`, `/admin/categories`, `/admin/achievements`, `/admin/difficulty-levels`, `/admin/interview-types`, `/admin/evaluation-criteria`, `/admin/audit-logs`

## Code conventions (non-obvious)

### Backend
- **Constructor injection** only — no `@Autowired`
- **UUID PKs** — `GenerationType.UUID` on all entities except `SessionQuestion` (`@GeneratedValue`)
- **Soft delete** — `deletedAt` on User + Question only; others use hard `deleteById()`
- **Timestamps** — `Instant`, UTC, via `@PrePersist`/`@PreUpdate`
- **Lombok** declared but **not used on entities** — manual getters/setters
- **`open-in-view=false`** — all DB access inside `@Transactional`
- **Entity relationships commented out** — no `@ManyToOne`/`@OneToMany` wired yet
- **Score fields** — `BigDecimal` across 4 dimensions: correctness, efficiency, logic, clarity
- **CORS**: hardcoded in `SecurityConfig.java` (`http://localhost:5173` only). The `spring.web.cors.allowed-origins` property in `application.properties` is **unused/dead**.

### Frontend
- **API file pattern**: fetch in `{domain}.ts` via `http.ts` → query keys via `createKeys()` in `{domain}.keys.ts` → React Query hooks in `{domain}.queries.ts`
- **`http.ts`**: Custom fetch wrapper; injects `Authorization: Bearer <token>`; 401 → clears auth + redirects to `/login`. No refresh token logic.
- **Auth in localStorage**: keys `"token"`, `"username"`, `"role"`
- **`QueryClient`**: `staleTime: 5min`, `retry: 1`, `refetchOnWindowFocus: false`
- **`@` alias** → `./src`
- **`verbatimModuleSyntax: true`** in tsconfig — forces `import type` for type-only imports
- **Tailwind v4** — CSS-first config (`@import "tailwindcss"` in `index.css`); no `tailwind.config.*` file; dark mode via `.dark` class
- **ESLint flat config** — `eslint.config.js` (no `.eslintrc*`)
- **No Prettier config** exists
- **`index.html`** lang is `es` (Spanish); fonts: JetBrains Mono, Manrope, Work Sans

## Security

- JWT stateless; BCrypt; JJWT 0.12.6
- `jwt.secret` and `jwt.expiration-ms` (24h) hardcoded in `application.properties`
- Only `POST /api/auth/**` is public — all other endpoints require `Authorization: Bearer <token>`
- `UserDetailsServiceImpl` loads users by email from DB

## Tests

- Only 1 backend test: `BackendApplicationTests` — context load (`@SpringBootTest`)
- No frontend tests (no Jest/Vitest/Testing Library in deps)

## Git workflow

- Active branch: `Develop` — all PRs target it (not `main`)
- Branches: `feature/*`, `fix/*`, `chore/*`, `docs/*`
- Conventional Commits: `<type>(<scope>): <description>` — types: feat, fix, refactor, test, chore, docs; scopes: entity, repository, service, controller, dto, config, db
- Rebase feature branches; squash-and-merge preferred
- PR checklist: `mvnw.cmd clean package -DskipTests` then `mvnw.cmd test`

## Existing docs

- `DEVELOP.md` — comprehensive Spanish Git/commit workflow guide (accurate)
- `frontend/GUIDE.md` — Tailwind v4 + shadcn/ui architecture guide for frontend devs
- `CLAUDE.md` — outdated; do not trust its architecture section (says layers "to be added" but they exist). Enums list is still accurate.
- `frontend/README.md` — generic Vite scaffold README (low value)
