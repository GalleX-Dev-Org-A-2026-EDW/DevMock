package com.devmock.backend.service.impl;

import com.devmock.backend.dto.CreateSessionQuestionRequest;
import com.devmock.backend.dto.UpdateSessionQuestionRequest;
import com.devmock.backend.dto.SessionQuestionResponse;
import com.devmock.backend.entity.SessionQuestion;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.SessionQuestionRepository;
import com.devmock.backend.service.SessionQuestionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SessionQuestionServiceImpl implements SessionQuestionService {

    private final SessionQuestionRepository repository;

    public SessionQuestionServiceImpl(SessionQuestionRepository repository) {
        this.repository = repository;
    }

    @Override
    public SessionQuestionResponse create(CreateSessionQuestionRequest request) {

        SessionQuestion s = new SessionQuestion();

        s.setQuestionOrder(request.getQuestionOrder());
        s.setAssignedTimeSeconds(request.getAssignedTimeSeconds());
        s.setTimeUsedSeconds(request.getTimeUsedSeconds());
        s.setUserAnswer(request.getUserAnswer());
        s.setObtainedPoints(request.getObtainedPoints());
        s.setCorrectnessScore(request.getCorrectnessScore());
        s.setEfficiencyScore(request.getEfficiencyScore());
        s.setLogicScore(request.getLogicScore());
        s.setClarityScore(request.getClarityScore());
        s.setEvaluationFeedback(request.getEvaluationFeedback());
        s.setAnsweredAt(request.getAnsweredAt());

        SessionQuestion saved = repository.save(s);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionQuestionResponse> list() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SessionQuestionResponse getById(UUID id) {

        SessionQuestion s = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("SessionQuestion " + id + " not found"));

        return toResponse(s);
    }

    @Override
    public SessionQuestionResponse update(UUID id, UpdateSessionQuestionRequest request) {

        SessionQuestion s = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("SessionQuestion " + id + " not found"));

        if (request.getQuestionOrder() != null)
            s.setQuestionOrder(request.getQuestionOrder());

        if (request.getAssignedTimeSeconds() != null)
            s.setAssignedTimeSeconds(request.getAssignedTimeSeconds());

        if (request.getTimeUsedSeconds() != null)
            s.setTimeUsedSeconds(request.getTimeUsedSeconds());

        if (request.getUserAnswer() != null)
            s.setUserAnswer(request.getUserAnswer());

        if (request.getObtainedPoints() != null)
            s.setObtainedPoints(request.getObtainedPoints());

        if (request.getCorrectnessScore() != null)
            s.setCorrectnessScore(request.getCorrectnessScore());

        if (request.getEfficiencyScore() != null)
            s.setEfficiencyScore(request.getEfficiencyScore());

        if (request.getLogicScore() != null)
            s.setLogicScore(request.getLogicScore());

        if (request.getClarityScore() != null)
            s.setClarityScore(request.getClarityScore());

        if (request.getEvaluationFeedback() != null)
            s.setEvaluationFeedback(request.getEvaluationFeedback());

        if (request.getAnsweredAt() != null)
            s.setAnsweredAt(request.getAnsweredAt());

        return toResponse(repository.save(s));
    }

    @Override
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "SessionQuestion " + id + " not found");
        }

        repository.deleteById(id);
    }

    // Mapper

    private SessionQuestionResponse toResponse(SessionQuestion s) {

        SessionQuestionResponse r = new SessionQuestionResponse();

        r.setId(s.getId());
        r.setQuestionOrder(s.getQuestionOrder());
        r.setAssignedTimeSeconds(s.getAssignedTimeSeconds());
        r.setTimeUsedSeconds(s.getTimeUsedSeconds());
        r.setUserAnswer(s.getUserAnswer());
        r.setObtainedPoints(s.getObtainedPoints());
        r.setCorrectnessScore(s.getCorrectnessScore());
        r.setEfficiencyScore(s.getEfficiencyScore());
        r.setLogicScore(s.getLogicScore());
        r.setClarityScore(s.getClarityScore());
        r.setEvaluationFeedback(s.getEvaluationFeedback());
        r.setAnsweredAt(s.getAnsweredAt());

        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());

        return r;
    }
}