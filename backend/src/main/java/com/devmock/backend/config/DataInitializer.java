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

        var easy = createDifficultyLevel("Easy", "easy", 1, BigDecimal.valueOf(1.00), "Basic questions");
        var medium = createDifficultyLevel("Medium", "medium", 2, BigDecimal.valueOf(1.50), "Intermediate questions");
        var hard = createDifficultyLevel("Hard", "hard", 3, BigDecimal.valueOf(2.00), "Advanced questions");

        var algorithms = createCategory("Algorithms", "algorithms", "Algorithm and data structure questions", "code-icon", 1);
        var dataStructures = createCategory("Data Structures", "data-structures", "Data structure implementation questions", "database-icon", 2);
        var webDev = createCategory("Web Development", "web-development", "Web development and API questions", "globe-icon", 3);
        var databases = createCategory("Databases", "databases", "Database and SQL questions", "server-icon", 4);
        var systemDesign = createCategory("System Design", "system-design", "System design and architecture questions", "layers-icon", 5);

        createInterviewType("Technical Interview", "technical-interview", QuestionType.MIXED, 5, 3600,
                "Standard technical interview with mixed question types");
        createInterviewType("Coding Challenge", "coding-challenge", QuestionType.PRACTICAL, 3, 1800,
                "Focused coding challenge with practical exercises");

        createEvaluationCriterion("Correctness", "correctness", "How correct is the solution", BigDecimal.valueOf(40.00));
        createEvaluationCriterion("Efficiency", "efficiency", "How efficient is the solution", BigDecimal.valueOf(25.00));
        createEvaluationCriterion("Clarity", "clarity", "How clear and readable is the code", BigDecimal.valueOf(20.00));
        createEvaluationCriterion("Logic", "logic", "How logical is the approach", BigDecimal.valueOf(15.00));

        var admin = createAdminUser(easy);

        createAchievement("First Session", "first-session", "Complete your first interview session",
                "complete 1 session", 50);
        createAchievement("Perfect Score", "perfect-score", "Get a perfect score on any question",
                "score 100 on any question", 100);
        createAchievement("Speed Demon", "speed-demon", "Complete a question in half the allotted time",
                "finish a question in under 50% of estimated time", 75);
        createAchievement("Marathon", "marathon", "Complete 10 interview sessions",
                "complete 10 sessions", 200);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Write a function that checks if a given string is a palindrome. The function should ignore case, spaces, and punctuation.",
                "def is_palindrome(s):\\n    cleaned = ''.join(c.lower() for c in s if c.isalnum())\\n    return cleaned == cleaned[::-1]",
                "Clean the string by keeping only alphanumeric characters, convert to lowercase, then compare with its reverse.",
                600, 50, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("palindrome", "strings", "easy"),
                algorithms, easy, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Implement a binary search tree (BST) with insert and search operations.",
                "class Node { int value; Node left, right; Node(int v) { value = v; } } class BST { Node insert(Node root, int v) { if (root == null) return new Node(v); if (v < root.value) root.left = insert(root.left, v); else root.right = insert(root.right, v); return root; } boolean search(Node root, int v) { if (root == null) return false; if (root.value == v) return true; return v < root.value ? search(root.left, v) : search(root.right, v); } }",
                "Each node has a value, left child, and right child. Insert recursively finds the correct position. Search recursively traverses left or right based on value comparison.",
                900, 100, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("bst", "trees", "data-structures"),
                dataStructures, medium, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Explain the key differences between REST and GraphQL APIs. When would you choose one over the other?",
                null,
                "REST uses fixed endpoints for resources, while GraphQL uses a single endpoint with a flexible query language. REST is simpler for basic CRUD, while GraphQL excels when clients need different data shapes.",
                600, 80, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.8}",
                List.of("rest", "graphql", "api", "web"),
                webDev, medium, admin);

        createQuestion(QuestionType.PRACTICAL, AnswerFormat.CODE,
                "Write a SQL query to find duplicate email addresses in a users table. The table has columns: id, name, email.",
                "SELECT email, COUNT(*) as count FROM users GROUP BY email HAVING COUNT(*) > 1;",
                "Group by email, count occurrences, filter groups with count greater than 1.",
                480, 80, "{\"timeWeight\": 0.3, \"correctnessWeight\": 0.7}",
                List.of("sql", "duplicates", "databases"),
                databases, medium, admin);

        createQuestion(QuestionType.THEORETICAL, AnswerFormat.FREE_TEXT,
                "Design a URL shortening service like TinyURL. Explain your system design choices including database schema, API design, and scaling considerations.",
                null,
                "Use a hash function (e.g., Base62 encoding of a unique ID) to generate short codes. Store mappings in a relational DB with the short code as primary key. Cache frequent lookups with Redis. Use a distributed counter (e.g., Snowflake) for unique ID generation across multiple servers.",
                1200, 150, "{\"timeWeight\": 0.2, \"correctnessWeight\": 0.5, \"designWeight\": 0.3}",
                List.of("system-design", "scalability", "url-shortener"),
                systemDesign, hard, admin);

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
