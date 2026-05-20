# DevMock

Mock interview / technical practice platform.

- **Java 25** / **Spring Boot 4.0.6**
- **PostgreSQL** (schema auto-managed via `ddl-auto=update`)
- **JWT stateless auth**

## How to run

```bash
cd backend
.\mvnw.cmd spring-boot:run    # Windows
```

Requires PostgreSQL running locally with database `devmock_db`. Configure credentials in `backend/.env`.

---

## API Reference

All endpoints (except `/api/auth/**`) require:

```
Authorization: Bearer <token>
```

---

### Auth — `/api/auth` (Público)

#### `POST /api/auth/register` — 201

```json
{
  "email": "test@example.com",
  "password": "password123",
  "fullName": "Test User",
  "role": "STUDENT",
  "avatarUrl": "https://example.com/avatar.jpg",
  "professionalExperienceYears": 2,
  "currentLevelId": "uuid-del-difficulty-level"
}
```

#### `POST /api/auth/login`

```json
{
  "email": "test@example.com",
  "password": "password123"
}
```

> Ambos devuelven: `{ "token", "email", "fullName" }`

---

### User — `/api/users`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/users` | Crear usuario (201) |
| GET | `/api/users` | Listar todos (filtra deletedAt=null) |
| GET | `/api/users/{id}` | Obtener por UUID |
| GET | `/api/users/by-email?email=` | Obtener por email |
| PUT | `/api/users/{id}` | Actualizar parcial |
| DELETE | `/api/users/{id}` | Soft delete (204) |

#### `POST /api/users`

```json
{
  "email": "dev@example.com",
  "password": "password123",
  "fullName": "Dev User",
  "role": "PROFESSIONAL",
  "professionalExperienceYears": 5,
  "currentLevelId": "uuid-del-difficulty-level"
}
```

#### `PUT /api/users/{id}`

```json
{
  "fullName": "Updated Name",
  "role": "ADMIN",
  "isActive": true
}
```

> **Soft delete:** solo se marca `deletedAt`, no se borra de BD.

---

### Category — `/api/categories`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/categories` | Crear (201) |
| GET | `/api/categories` | Listar (`?activeOnly=true`) |
| GET | `/api/categories/{id}` | Obtener por UUID |
| GET | `/api/categories/by-slug/{slug}` | Obtener por slug |
| PUT | `/api/categories/{id}` | Actualizar |
| DELETE | `/api/categories/{id}` | Hard delete (204) |

#### `POST /api/categories`

```json
{
  "name": "Algorithms",
  "slug": "algorithms",
  "description": "Algorithm and data structure questions",
  "icon": "code-icon",
  "displayOrder": 1,
  "parentId": "uuid-de-la-categoria-padre"
}
```

#### `PUT /api/categories/{id}`

```json
{
  "name": "Advanced Algorithms",
  "isActive": true
}
```

> **Slug:** único, solo minúsculas/números/guiones. `slug` duplicado → `SLUG_ALREADY_EXISTS`.

---

### DifficultyLevel — `/api/difficulty-levels`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/difficulty-levels` | Crear (201) |
| GET | `/api/difficulty-levels` | Listar ordenado por levelOrder |
| GET | `/api/difficulty-levels/{id}` | Obtener por UUID |
| GET | `/api/difficulty-levels/by-slug/{slug}` | Obtener por slug |
| PUT | `/api/difficulty-levels/{id}` | Actualizar |
| DELETE | `/api/difficulty-levels/{id}` | Hard delete (204) |

#### `POST /api/difficulty-levels`

```json
{
  "name": "Easy",
  "slug": "easy",
  "levelOrder": 1,
  "pointsMultiplier": 1.00,
  "description": "Basic questions"
}
```

```json
{
  "name": "Medium",
  "slug": "medium",
  "levelOrder": 2,
  "pointsMultiplier": 1.50
}
```

```json
{
  "name": "Hard",
  "slug": "hard",
  "levelOrder": 3,
  "pointsMultiplier": 2.00
}
```

> Validaciones: `name`, `slug` y `levelOrder` son únicos individualmente. `LEVEL_ORDER_ALREADY_EXISTS`, `NAME_ALREADY_EXISTS`.

---

### InterviewType — `/api/interview-types`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/interview-types` | Crear (201) |
| GET | `/api/interview-types` | Listar (`?activeOnly=true`) |
| GET | `/api/interview-types/{id}` | Obtener por UUID |
| GET | `/api/interview-types/by-slug/{slug}` | Obtener por slug |
| PUT | `/api/interview-types/{id}` | Actualizar |
| DELETE | `/api/interview-types/{id}` | Hard delete (204) |

#### `POST /api/interview-types`

```json
{
  "name": "Technical Interview",
  "slug": "technical-interview",
  "questionType": "MIXED",
  "totalQuestions": 5,
  "totalTimeSeconds": 3600,
  "description": "Standard technical interview"
}
```

---

### Question — `/api/questions`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/questions` | Crear (201) |
| GET | `/api/questions` | Listar (filtra deletedAt=null) |
| GET | `/api/questions/{id}` | Obtener por UUID |
| PUT | `/api/questions/{id}` | Actualizar |
| DELETE | `/api/questions/{id}` | Soft delete (204) |

#### `POST /api/questions`

```json
{
  "questionType": "PRACTICAL",
  "answerFormat": "CODE",
  "statement": "Write a function that reverses a linked list.",
  "expectedAnswer": "def reverse_list(head): ...",
  "explanation": "Use three pointers to reverse in-place.",
  "estimatedTimeSeconds": 600,
  "basePoints": 100,
  "evaluationConfig": "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
  "tags": ["linked-list", "algorithms", "python"],
  "categoryId": "uuid-de-la-categoria",
  "createdById": "uuid-del-usuario-creador"
}
```

#### `PUT /api/questions/{id}`

```json
{
  "basePoints": 150,
  "isActive": false
}
```

> **Soft delete:** marca `deletedAt` + `isActive = false`.

---

### AnswerOption — `/api/answer-options`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/answer-options` | Crear (201) |
| GET | `/api/answer-options` | Listar |
| GET | `/api/answer-options/{id}` | Obtener por UUID |
| PUT | `/api/answer-options/{id}` | Actualizar |
| DELETE | `/api/answer-options/{id}` | Hard delete (204) |

#### `POST /api/answer-options`

```json
{
  "optionText": "O(n)",
  "isCorrect": true,
  "explanation": "Linear time complexity",
  "displayOrder": 1
}
```

---

### EvaluationCriterion — `/api/evaluation-criteria`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/evaluation-criteria` | Crear (201) |
| GET | `/api/evaluation-criteria` | Listar (`?activeOnly=true`) |
| GET | `/api/evaluation-criteria/{id}` | Obtener por UUID |
| GET | `/api/evaluation-criteria/by-slug/{slug}` | Obtener por slug |
| PUT | `/api/evaluation-criteria/{id}` | Actualizar |
| DELETE | `/api/evaluation-criteria/{id}` | Hard delete (204) |

#### `POST /api/evaluation-criteria`

```json
{
  "name": "Correctness",
  "slug": "correctness",
  "description": "How correct is the solution",
  "defaultWeight": 40.00
}
```

```json
{
  "name": "Efficiency",
  "slug": "efficiency",
  "defaultWeight": 25.00
}
```

```json
{
  "name": "Clarity",
  "slug": "clarity",
  "defaultWeight": 20.00
}
```

```json
{
  "name": "Logic",
  "slug": "logic",
  "defaultWeight": 15.00
}
```

---

### QuestionCriterion — `/api/question-criteria`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/question-criteria` | Crear (201) |
| GET | `/api/question-criteria` | Listar |
| GET | `/api/question-criteria/{id}` | Obtener por UUID |
| PUT | `/api/question-criteria/{id}` | Actualizar |
| DELETE | `/api/question-criteria/{id}` | Hard delete (204) |

#### `POST /api/question-criteria`

```json
{
  "weight": 40.00
}
```

---

### InterviewSession — `/api/interview-sessions`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/interview-sessions` | Crear (201) |
| GET | `/api/interview-sessions` | Listar |
| GET | `/api/interview-sessions/{id}` | Obtener por UUID |
| PUT | `/api/interview-sessions/{id}` | Actualizar |
| DELETE | `/api/interview-sessions/{id}` | Hard delete (204) |

#### `POST /api/interview-sessions`

```json
{
  "status": "IN_PROGRESS",
  "startedAt": "2026-05-20T10:00:00Z",
  "totalTimeUsedSeconds": 0
}
```

---

### SessionQuestion — `/api/session-questions`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/session-questions` | Crear (201) |
| GET | `/api/session-questions` | Listar |
| GET | `/api/session-questions/{id}` | Obtener por UUID |
| PUT | `/api/session-questions/{id}` | Actualizar |
| DELETE | `/api/session-questions/{id}` | Hard delete (204) |

#### `POST /api/session-questions`

```json
{
  "questionOrder": 1,
  "assignedTimeSeconds": 600,
  "timeUsedSeconds": 450,
  "userAnswer": "def reverse_list(head): ...",
  "obtainedPoints": 85.00,
  "correctnessScore": 80.00,
  "efficiencyScore": 90.00,
  "logicScore": 85.00,
  "clarityScore": 85.00,
  "evaluationFeedback": "Good solution, but could be optimized.",
  "answeredAt": "2026-05-20T10:07:30Z"
}
```

---

### Achievement — `/api/achievements`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/achievements` | Crear (201) |
| GET | `/api/achievements` | Listar |
| GET | `/api/achievements/{id}` | Obtener por UUID |
| PUT | `/api/achievements/{id}` | Actualizar |
| DELETE | `/api/achievements/{id}` | Hard delete (204) |

#### `POST /api/achievements`

```json
{
  "name": "First Session",
  "slug": "first-session",
  "description": "Complete your first interview session",
  "iconUrl": "https://example.com/badge.png",
  "unlockCriteria": "complete 1 session",
  "pointsReward": 50,
  "isActive": true
}
```

> `slug` es único.

---

### UserAchievement — `/api/user-achievements`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/user-achievements` | Crear (201) |
| GET | `/api/user-achievements` | Listar |
| GET | `/api/user-achievements/{id}` | Obtener por UUID |
| PUT | `/api/user-achievements/{id}` | Actualizar |
| DELETE | `/api/user-achievements/{id}` | Hard delete (204) |

#### `POST /api/user-achievements`

```json
{
  "unlockedAt": "2026-05-20T12:00:00Z",
  "isViewed": false
}
```

---

### UserPerformance — `/api/user-performances`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/user-performances` | Crear (201) |
| GET | `/api/user-performances` | Listar |
| GET | `/api/user-performances/{id}` | Obtener por UUID |
| PUT | `/api/user-performances/{id}` | Actualizar |
| DELETE | `/api/user-performances/{id}` | Hard delete (204) |

#### `POST /api/user-performances`

```json
{
  "totalQuestionsAnswered": 10,
  "totalCorrect": 8,
  "accuracyPercentage": 80.00,
  "avgTimeSeconds": 120.50,
  "avgScore": 85.00,
  "strengths": "Algorithms, Data Structures",
  "weaknesses": "Dynamic Programming",
  "lastPracticedAt": "2026-05-20T12:00:00Z"
}
```

---

### Ranking — `/api/rankings`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/rankings` | Crear (201) |
| GET | `/api/rankings` | Listar |
| GET | `/api/rankings/{id}` | Obtener por UUID |
| PUT | `/api/rankings/{id}` | Actualizar |
| DELETE | `/api/rankings/{id}` | Hard delete (204) |

#### `POST /api/rankings`

```json
{
  "period": "WEEKLY",
  "periodStartDate": "2026-05-18",
  "periodEndDate": "2026-05-24",
  "totalScore": 1500.00,
  "totalSessions": 5,
  "rankPosition": 1,
  "calculatedAt": "2026-05-24T23:59:59Z"
}
```

> `period` enum: `WEEKLY`, `MONTHLY`, `ALL_TIME`.

---

### AuditLog — `/api/audit-logs`

| Method | Path | Descripción |
|--------|------|-------------|
| POST | `/api/audit-logs` | Crear (201) |
| GET | `/api/audit-logs` | Listar |
| GET | `/api/audit-logs/{id}` | Obtener por UUID |
| PUT | `/api/audit-logs/{id}` | Actualizar |
| DELETE | `/api/audit-logs/{id}` | Hard delete (204) |

#### `POST /api/audit-logs`

```json
{
  "action": "LOGIN",
  "entityName": "User",
  "entityId": "00000000-0000-0000-0000-000000000000",
  "oldValues": null,
  "newValues": null,
  "ipAddress": "192.168.1.1",
  "userAgent": "Postman"
}
```

> `action` enum: `CREATE`, `UPDATE`, `DELETE`, `LOGIN`, `LOGOUT`.

---

## Quick test sequence (Postman)

1. **Register** → `POST /api/auth/register` → copy `token`
2. **Create categories** → `POST /api/categories` (3-4 categories)
3. **Create difficulty levels** → `POST /api/difficulty-levels` (Easy, Medium, Hard)
4. **Create interview types** → `POST /api/interview-types`
5. **Create evaluation criteria** → `POST /api/evaluation-criteria` (Correctness, Efficiency, Clarity, Logic)
6. **Create questions** → `POST /api/questions`
7. **Create answer options** → `POST /api/answer-options`
8. **Create session** → `POST /api/interview-sessions`
9. **Link question to session** → `POST /api/session-questions`

Use `Authorization: Bearer <token>` in all headers after step 1.
