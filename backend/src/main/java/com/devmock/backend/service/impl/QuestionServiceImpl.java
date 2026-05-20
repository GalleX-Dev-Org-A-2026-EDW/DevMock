package com.devmock.backend.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devmock.backend.dto.CreateQuestionRequest;
import com.devmock.backend.dto.QuestionResponse;
import com.devmock.backend.dto.UpdateQuestionRequest;
import com.devmock.backend.entity.Question;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.QuestionRepository;
import com.devmock.backend.service.QuestionService;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository repository;

    public QuestionServiceImpl(QuestionRepository repository) {
        this.repository = repository;
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
        r.setCreatedAt(q.getCreatedAt());
        r.setUpdatedAt(q.getUpdatedAt());
        return r;
    }
}
