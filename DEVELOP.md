# Guía de Desarrollo — DevMock

## Tabla de contenido

1. [Requisitos previos](#requisitos-previos)
2. [Configuración del entorno local](#configuración-del-entorno-local)
3. [Estructura del repositorio](#estructura-del-repositorio)
4. [Estrategia de ramas](#estrategia-de-ramas)
5. [Flujo de trabajo Git](#flujo-de-trabajo-git)
6. [Convenciones de commits](#convenciones-de-commits)
7. [Pull Requests](#pull-requests)
8. [Levantar el proyecto](#levantar-el-proyecto)

---

## Requisitos previos

| Herramienta | Versión mínima |
|-------------|---------------|
| Java JDK    | 25            |
| Maven       | incluido (`mvnw`) |
| PostgreSQL  | 14+           |
| Git         | 2.x           |

---

## Configuración del entorno local

1. Clonar el repositorio:
   ```bash
   git clone <url-del-repositorio>
   cd DevMock
   ```

2. Crear la base de datos PostgreSQL:
   ```sql
   CREATE DATABASE devmock_db;
   ```
   Usuario: `postgres` / Contraseña: `12345678` (configuración local por defecto).

3. Levantar el backend:
   ```bash
   cd backend
   ./mvnw spring-boot:run        # Linux / macOS
   mvnw.cmd spring-boot:run      # Windows
   ```

4. Verificar que la app inició en `http://localhost:8080`.

---

## Estructura del repositorio

```
DevMock/
├── backend/          # Spring Boot — API REST
│   ├── src/
│   │   └── main/java/com/devmock/backend/
│   │       ├── entity/       # Entidades JPA
│   │       │   └── en_enum/  # Enumeraciones de dominio
│   │       ├── repository/   # Repositorios Spring Data
│   │       ├── service/      # Lógica de negocio
│   │       ├── controller/   # Controladores REST
│   │       └── dto/          # DTOs de request/response
│   └── pom.xml
├── CLAUDE.md         # Instrucciones para Claude Code
└── DEVELOP.md        # Esta guía
```

---

## Estrategia de ramas

El proyecto usa un flujo basado en **Git Flow simplificado** con dos ramas principales:

```
main
 └── Develop
      ├── feature/nombre-de-la-feature
      ├── fix/descripcion-del-bug
      └── chore/tarea-de-mantenimiento
```

### Ramas principales

| Rama      | Propósito                                      | Protegida |
|-----------|------------------------------------------------|-----------|
| `main`    | Código estable / producción. Solo recibe merges desde `Develop` | Sí |
| `Develop` | Rama de integración activa. Aquí se abren todos los PRs | Sí |

> **Regla de oro:** nunca hacer commits directos a `main` ni a `Develop`. Todo cambio entra por Pull Request.

### Ramas de trabajo

Crear siempre desde `Develop`:

```bash
git checkout Develop
git pull origin Develop
git checkout -b feature/nombre-descriptivo
```

#### Convención de nombres

| Prefijo     | Cuándo usarlo                                     | Ejemplo                          |
|-------------|---------------------------------------------------|----------------------------------|
| `feature/`  | Nueva funcionalidad                               | `feature/auth-jwt`               |
| `fix/`      | Corrección de bug                                 | `fix/session-timeout`            |
| `chore/`    | Refactor, dependencias, configuración             | `chore/update-dependencies`      |
| `docs/`     | Solo documentación                                | `docs/api-endpoints`             |

---

## Flujo de trabajo Git

### 1. Iniciar una tarea

```bash
# Asegurarse de tener Develop actualizado
git checkout Develop
git pull origin Develop

# Crear la rama de trabajo
git checkout -b feature/mi-nueva-feature
```

### 2. Desarrollar y hacer commits

```bash
# Agregar cambios (nunca usar git add . ciegamente)
git add backend/src/main/java/com/devmock/backend/entity/User.java

# Commit siguiendo la convención (ver sección siguiente)
git commit -m "feat(entity): add User JPA entity with roles"
```

### 3. Mantener la rama actualizada

Si `Develop` avanzó mientras trabajabas:

```bash
git fetch origin
git rebase origin/Develop
```

Usar `rebase` en lugar de `merge` para mantener un historial limpio en las ramas de feature.

### 4. Publicar la rama y abrir PR

```bash
git push origin feature/mi-nueva-feature
```

Luego abrir el Pull Request apuntando a `Develop` (nunca a `main`).

### 5. Después del merge

```bash
# Volver a Develop y limpiar la rama local
git checkout Develop
git pull origin Develop
git branch -d feature/mi-nueva-feature
```

---

## Convenciones de commits

Se sigue el estándar **Conventional Commits**:

```
<tipo>(<alcance>): <descripción breve en minúsculas>
```

| Tipo       | Cuándo usarlo                                  |
|------------|------------------------------------------------|
| `feat`     | Nueva funcionalidad                            |
| `fix`      | Corrección de bug                              |
| `refactor` | Cambio de código que no agrega ni arregla nada |
| `test`     | Agregar o modificar pruebas                    |
| `chore`    | Tareas de mantenimiento, dependencias          |
| `docs`     | Solo documentación                             |

**Alcances sugeridos:** `entity`, `repository`, `service`, `controller`, `dto`, `config`, `db`

### Ejemplos

```
feat(controller): add POST /sessions endpoint
fix(service): correct score calculation for expired sessions
test(repository): add UserRepository integration tests
chore(config): update application.properties for prod profile
```

### Reglas adicionales

- La descripción va en **inglés**, en tiempo presente (`add`, no `added` ni `adds`).
- Máximo 72 caracteres en la primera línea.
- Si el cambio es extenso, agregar cuerpo separado por una línea en blanco.

---

## Pull Requests

### Antes de abrir un PR

- [ ] El código compila sin errores: `mvnw.cmd clean package -DskipTests`
- [ ] Los tests existentes pasan: `mvnw.cmd test`
- [ ] No hay archivos de configuración sensibles incluidos (contraseñas, tokens)
- [ ] La rama fue rebaseada contra `Develop` recientemente

### Plantilla de PR

**Título:** seguir la misma convención de commits (`feat(entity): ...`).

**Descripción mínima:**
```
## ¿Qué hace este PR?
Breve descripción del cambio.

## ¿Cómo probarlo?
Pasos para verificar que funciona.

## Checklist
- [ ] Compilación exitosa
- [ ] Tests pasando
- [ ] Sin datos sensibles
```

### Revisión y merge

- Todo PR requiere al menos **una aprobación** antes de hacer merge.
- Usar **Squash and Merge** cuando los commits intermedios no aportan valor al historial.
- Usar **Merge Commit** cuando se quiere preservar el historial de la feature.

---

## Levantar el proyecto

### Backend (desarrollo)

```bash
cd backend
mvnw.cmd spring-boot:run          # Inicia con hot-reload en :8080
```

### Comandos útiles

```bash
# Build sin tests
mvnw.cmd clean package -DskipTests

# Ejecutar todos los tests
mvnw.cmd test

# Ejecutar una clase de test específica
mvnw.cmd test -Dtest=UserServiceTest

# Ejecutar un método de test específico
mvnw.cmd test -Dtest=UserServiceTest#shouldReturnUserById
```

### Variables de entorno (opcional)

Para sobreescribir la configuración de `application.properties` sin modificar el archivo:

```bash
set DB_URL=jdbc:postgresql://localhost:5432/devmock_db
set DB_USER=postgres
set DB_PASS=12345678
mvnw.cmd spring-boot:run
```
