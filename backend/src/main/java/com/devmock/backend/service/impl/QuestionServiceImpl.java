package com.devmock.backend.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devmock.backend.dto.CreateQuestionRequest;
import com.devmock.backend.dto.QuestionResponse;
import com.devmock.backend.dto.UpdateQuestionRequest;
import com.devmock.backend.entity.Category;
import com.devmock.backend.entity.DifficultyLevel;
import com.devmock.backend.entity.Question;
import com.devmock.backend.entity.User;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.CategoryRepository;
import com.devmock.backend.repository.DifficultyLevelRepository;
import com.devmock.backend.repository.QuestionRepository;
import com.devmock.backend.repository.UserRepository;
import com.devmock.backend.service.QuestionService;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DifficultyLevelRepository difficultyLevelRepository;

    public QuestionServiceImpl(QuestionRepository repository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            DifficultyLevelRepository difficultyLevelRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
    }

    @Override
    public QuestionResponse create(CreateQuestionRequest request) {
        Question q = new Question();
        q.setQuestionType(request.getQuestionType());
        q.setAnswerFormat(request.getAnswerFormat());
        q.setStatement(request.getStatement());
        q.setExpectedAnswer(request.getExpectedAnswer());
        q.setExplanation(request.getExplanation());
        q.setEstimatedTimeSeconds(request.getEstimatedTimeSeconds());
        q.setBasePoints(request.getBasePoints());
        q.setEvaluationConfig(request.getEvaluationConfig());
        q.setTags(request.getTags());

        if (request.getCreatedById() != null) {
            User user = userRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User " + request.getCreatedById() + " not found"));
            q.setCreatedBy(user);
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category " + request.getCategoryId() + " not found"));
            q.setCategory(category);
        }
        if (request.getDifficultyId() != null) {
            DifficultyLevel level = difficultyLevelRepository.findById(request.getDifficultyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DifficultyLevel " + request.getDifficultyId() + " not found"));
            q.setDifficulty(level);
        }

        return toResponse(repository.save(q));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> list() {
        return repository.findAll().stream()
                .filter(q -> q.getDeletedAt() == null)
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getById(UUID id) {
        Question q = repository.findById(id)
                .filter(q2 -> q2.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Question " + id + " not found"));
        return toResponse(q);
    }

    @Override
    public QuestionResponse update(UUID id, UpdateQuestionRequest request) {
        Question q = repository.findById(id)
                .filter(q2 -> q2.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Question " + id + " not found"));
        if (request.getQuestionType() != null) q.setQuestionType(request.getQuestionType());
        if (request.getAnswerFormat() != null) q.setAnswerFormat(request.getAnswerFormat());
        if (request.getStatement() != null) q.setStatement(request.getStatement());
        if (request.getExpectedAnswer() != null) q.setExpectedAnswer(request.getExpectedAnswer());
        if (request.getExplanation() != null) q.setExplanation(request.getExplanation());
        if (request.getEstimatedTimeSeconds() != null) q.setEstimatedTimeSeconds(request.getEstimatedTimeSeconds());
        if (request.getBasePoints() != null) q.setBasePoints(request.getBasePoints());
        if (request.getEvaluationConfig() != null) q.setEvaluationConfig(request.getEvaluationConfig());
        if (request.getTags() != null) q.setTags(request.getTags());
        if (request.getIsActive() != null) q.setIsActive(request.getIsActive());

        if (request.getCreatedById() != null) {
            User user = userRepository.findById(request.getCreatedById())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User " + request.getCreatedById() + " not found"));
            q.setCreatedBy(user);
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category " + request.getCategoryId() + " not found"));
            q.setCategory(category);
        }
        if (request.getDifficultyId() != null) {
            DifficultyLevel level = difficultyLevelRepository.findById(request.getDifficultyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DifficultyLevel " + request.getDifficultyId() + " not found"));
            q.setDifficulty(level);
        }

        return toResponse(repository.save(q));
    }

    @Override
    public void delete(UUID id) {
        Question q = repository.findById(id)
                .filter(q2 -> q2.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Question " + id + " not found"));
        q.setDeletedAt(Instant.now());
        q.setIsActive(false);
        repository.save(q);
    }

    private QuestionResponse toResponse(Question q) {
        QuestionResponse r = new QuestionResponse();
        r.setId(q.getId());
        r.setQuestionType(q.getQuestionType());
        r.setAnswerFormat(q.getAnswerFormat());
        r.setStatement(q.getStatement());
        r.setExpectedAnswer(q.getExpectedAnswer());
        r.setExplanation(q.getExplanation());
        r.setEstimatedTimeSeconds(q.getEstimatedTimeSeconds());
        r.setBasePoints(q.getBasePoints());
        r.setEvaluationConfig(q.getEvaluationConfig());
        r.setTags(q.getTags());
        r.setIsActive(q.getIsActive());
        if (q.getCreatedBy() != null) {
            r.setCreatedById(q.getCreatedBy().getId());
        }
        if (q.getCategory() != null) {
            r.setCategoryId(q.getCategory().getId());
        }
        if (q.getDifficulty() != null) {
            r.setDifficultyId(q.getDifficulty().getId());
        }
        r.setCreatedAt(q.getCreatedAt());
        r.setUpdatedAt(q.getUpdatedAt());
        return r;
    }
}
