package com.devmock.backend.config;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.devmock.backend.entity.Achievement;
import com.devmock.backend.entity.Category;
import com.devmock.backend.entity.DifficultyLevel;
import com.devmock.backend.entity.EvaluationCriterion;
import com.devmock.backend.entity.InterviewType;
import com.devmock.backend.entity.Question;
import com.devmock.backend.entity.User;
import com.devmock.backend.entity.en_enum.AnswerFormat;
import com.devmock.backend.entity.en_enum.QuestionType;
import com.devmock.backend.entity.en_enum.UserRole;
import com.devmock.backend.repository.AchievementRepository;
import com.devmock.backend.repository.CategoryRepository;
import com.devmock.backend.repository.DifficultyLevelRepository;
import com.devmock.backend.repository.EvaluationCriterionRepository;
import com.devmock.backend.repository.InterviewTypeRepository;
import com.devmock.backend.repository.QuestionRepository;
import com.devmock.backend.repository.UserRepository;

@Component
@Transactional
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepo;
    private final DifficultyLevelRepository difficultyLevelRepo;
    private final CategoryRepository categoryRepo;
    private final InterviewTypeRepository interviewTypeRepo;
    private final EvaluationCriterionRepository evaluationCriterionRepo;
    private final AchievementRepository achievementRepo;
    private final QuestionRepository questionRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepo,
            DifficultyLevelRepository difficultyLevelRepo,
            CategoryRepository categoryRepo,
            InterviewTypeRepository interviewTypeRepo,
            EvaluationCriterionRepository evaluationCriterionRepo,
            AchievementRepository achievementRepo,
            QuestionRepository questionRepo,
            PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.difficultyLevelRepo = difficultyLevelRepo;
        this.categoryRepo = categoryRepo;
        this.interviewTypeRepo = interviewTypeRepo;
        this.evaluationCriterionRepo = evaluationCriterionRepo;
        this.achievementRepo = achievementRepo;
        this.questionRepo = questionRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (difficultyLevelRepo.count() > 0) {
            log.info("Data already seeded — skipping");
            return;
        }

        log.info("Seeding initial data...");

        var facil = createDifficultyLevel("Fácil", "facil", 1, BigDecimal.valueOf(1.00), "Preguntas básicas");
        var intermedio = createDifficultyLevel("Intermedio", "intermedio", 2, BigDecimal.valueOf(1.50), "Preguntas intermedias");
        var avanzado = createDifficultyLevel("Avanzado", "avanzado", 3, BigDecimal.valueOf(2.00), "Preguntas avanzadas");

        var algoritmos = createCategory("Algoritmos", "algoritmos", "Preguntas sobre algoritmos y estructuras de datos", "code-icon", 1);
        var estructurasDatos = createCategory("Estructuras de Datos", "estructuras-de-datos", "Preguntas de implementación de estructuras de datos", "database-icon", 2);
        var desarrolloWeb = createCategory("Desarrollo Web", "desarrollo-web", "Preguntas sobre desarrollo web y APIs", "globe-icon", 3);
        var basesDatos = createCategory("Bases de Datos", "bases-de-datos", "Preguntas sobre bases de datos y SQL", "server-icon", 4);
        var disenoSistemas = createCategory("Diseño de Sistemas", "diseno-de-sistemas", "Preguntas sobre diseño de sistemas y arquitectura", "layers-icon", 5);

        createInterviewType("Entrevista Técnica", "entrevista-tecnica", QuestionType.MIXED, 5, 3600,
                "Entrevista técnica estándar con tipos de preguntas mixtas");
        createInterviewType("Desafío de Código", "desafio-de-codigo", QuestionType.PRACTICAL, 3, 1800,
                "Desafío de código enfocado en ejercicios prácticos");

        createEvaluationCriterion("Corrección", "correccion", "Qué tan correcta es la solución", BigDecimal.valueOf(40.00));
        createEvaluationCriterion("Eficiencia", "eficiencia", "Qué tan eficiente es la solución", BigDecimal.valueOf(25.00));
        createEvaluationCriterion("Claridad", "claridad", "Qué tan claro y legible es el código", BigDecimal.valueOf(20.00));
        createEvaluationCriterion("Lógica", "logica", "Qué tan lógico es el enfoque", BigDecimal.valueOf(15.00));

        var admin = createAdminUser(facil);

        createAchievement("Primera Sesión", "primera-sesion", "Completa tu primera sesión de entrevista",
                "completar 1 sesión", 50);
        createAchievement("Puntuación Perfecta", "puntuacion-perfecta", "Obtén una puntuación perfecta en cualquier pregunta",
                "puntuar 100 en cualquier pregunta", 100);
        createAchievement("Rápido como un Rayo", "rapido-como-un-rayo", "Completa una pregunta en la mitad del tiempo asignado",
                "terminar una pregunta en menos del 50% del tiempo estimado", 75);
        createAchievement("Maratón", "maraton", "Completa 10 sesiones de entrevista",
                "completar 10 sesiones", 200);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una función que verifique si una cadena dada es un palíndromo. La función debe ignorar mayúsculas, espacios y signos de puntuación.",
                "def is_palindrome(s):\\n    cleaned = ''.join(c.lower() for c in s if c.isalnum())\\n    return cleaned == cleaned[::-1]",
                "Limpia la cadena conservando solo caracteres alfanuméricos, convierte a minúsculas, luego compara con su reverso.",
                600, 50, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("palindromo", "cadenas", "facil"),
                algoritmos, facil, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implementa un árbol binario de búsqueda (BST) con operaciones de inserción y búsqueda.",
                "class Node { int value; Node left, right; Node(int v) { value = v; } } class BST { Node insert(Node root, int v) { if (root == null) return new Node(v); if (v < root.value) root.left = insert(root.left, v); else root.right = insert(root.right, v); return root; } boolean search(Node root, int v) { if (root == null) return false; if (root.value == v) return true; return v < root.value ? search(root.left, v) : search(root.right, v); } }",
                "Cada nodo tiene un valor, un hijo izquierdo y un hijo derecho. Insertar busca recursivamente la posición correcta. Buscar recorre recursivamente izquierda o derecha según la comparación de valores.",
                900, 100, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("bst", "arboles", "estructuras-de-datos"),
                estructurasDatos, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explica las diferencias clave entre las APIs REST y GraphQL. ¿Cuándo elegirías una sobre la otra?",
                null,
                "REST usa endpoints fijos para los recursos, mientras que GraphQL usa un solo endpoint con un lenguaje de consulta flexible. REST es más simple para CRUD básico, mientras que GraphQL destaca cuando los clientes necesitan diferentes formas de datos.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("rest", "graphql", "api", "web"),
                desarrolloWeb, intermedio, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Escribe una consulta SQL para encontrar direcciones de email duplicadas en una tabla de usuarios. La tabla tiene las columnas: id, name, email.",
                "SELECT email, COUNT(*) as count FROM usuarios GROUP BY email HAVING COUNT(*) > 1;",
                "Agrupa por email, cuenta las ocurrencias, filtra los grupos con count mayor a 1.",
                480, 80, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("sql", "duplicados", "bases-de-datos"),
                basesDatos, intermedio, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Diseña un servicio de acortamiento de URLs como TinyURL. Explica tus decisiones de diseño incluyendo el esquema de base de datos, diseño de API y consideraciones de escalabilidad.",
                null,
                "Usa una función hash (ej. codificación Base62 de un ID único) para generar códigos cortos. Almacena los mapeos en una BD relacional con el código corto como clave primaria. Cachea las búsquedas frecuentes con Redis. Usa un contador distribuido (ej. Snowflake) para generación de IDs únicos en múltiples servidores.",
                1200, 150, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.5, \"designWeight\": 0.3}",
                List.of("diseno-de-sistemas", "escalabilidad", "acortador-urls"),
                disenoSistemas, avanzado, admin);

        log.info("Seed complete — {} users, {} difficulty levels, {} categories, {} questions",
                userRepo.count(), difficultyLevelRepo.count(), categoryRepo.count(), questionRepo.count());
    }

    private DifficultyLevel createDifficultyLevel(String name, String slug, int order, BigDecimal multiplier, String desc) {
        var dl = new DifficultyLevel();
        dl.setName(name);
        dl.setSlug(slug);
        dl.setLevelOrder(order);
        dl.setPointsMultiplier(multiplier);
        dl.setDescription(desc);
        return difficultyLevelRepo.save(dl);
    }

    private Category createCategory(String name, String slug, String desc, String icon, int order) {
        var c = new Category();
        c.setName(name);
        c.setSlug(slug);
        c.setDescription(desc);
        c.setIcon(icon);
        c.setDisplayOrder(order);
        c.setIsActive(true);
        return categoryRepo.save(c);
    }

    private InterviewType createInterviewType(String name, String slug, QuestionType qtype, int totalQ, int totalTime, String desc) {
        var it = new InterviewType();
        it.setName(name);
        it.setSlug(slug);
        it.setQuestionType(qtype);
        it.setTotalQuestions(totalQ);
        it.setTotalTimeSeconds(totalTime);
        it.setDescription(desc);
        it.setIsActive(true);
        return interviewTypeRepo.save(it);
    }

    private EvaluationCriterion createEvaluationCriterion(String name, String slug, String desc, BigDecimal weight) {
        var ec = new EvaluationCriterion();
        ec.setName(name);
        ec.setSlug(slug);
        ec.setDescription(desc);
        ec.setDefaultWeight(weight);
        ec.setIsActive(true);
        return evaluationCriterionRepo.save(ec);
    }

    private User createAdminUser(DifficultyLevel level) {
        var admin = new User();
        admin.setEmail("admin@devmock.com");
        admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
        admin.setFullName("Admin DevMock");
        admin.setRole(UserRole.ADMIN);
        admin.setIsActive(true);
        admin.setIsVerified(true);
        admin.setCurrentLevel(level);
        return userRepo.save(admin);
    }

    private Achievement createAchievement(String name, String slug, String desc, String criteria, int points) {
        var a = new Achievement();
        a.setName(name);
        a.setSlug(slug);
        a.setDescription(desc);
        a.setUnlockCriteria(criteria);
        a.setPointsReward(points);
        a.setIsActive(true);
        return achievementRepo.save(a);
    }

    private void createQuestion(QuestionType qtype, AnswerFormat fmt, String statement, String answer, String explanation,
            int estimatedTime, int basePoints, String evalConfig, List<String> tags,
            Category category, DifficultyLevel difficulty, User createdBy) {

        var q = new Question();
        q.setQuestionType(qtype);
        q.setAnswerFormat(fmt);
        q.setStatement(statement);
        q.setExpectedAnswer(answer);
        q.setExplanation(explanation);
        q.setEstimatedTimeSeconds(estimatedTime);
        q.setBasePoints(basePoints);
        q.setEvaluationConfig(evalConfig);
        q.setTags(tags);
        q.setCategory(category);
        q.setDifficulty(difficulty);
        q.setCreatedBy(createdBy);
        q.setIsActive(true);
        questionRepo.save(q);
    }
}
