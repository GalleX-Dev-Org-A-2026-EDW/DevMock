package com.devmock.backend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devmock.backend.dto.CreateQuestionCriterionRequest;
import com.devmock.backend.dto.QuestionCriterionResponse;
import com.devmock.backend.dto.UpdateQuestionCriterionRequest;
import com.devmock.backend.entity.QuestionCriterion;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.QuestionCriterionRepository;
import com.devmock.backend.service.QuestionCriterionService;

@Service
@Transactional
public class QuestionCriterionServiceImpl implements QuestionCriterionService {

    private final QuestionCriterionRepository repository;

    public QuestionCriterionServiceImpl(QuestionCriterionRepository repository) {
        this.repository = repository;
    }

    @Override
    public QuestionCriterionResponse create(CreateQuestionCriterionRequest request) {
        QuestionCriterion qc = new QuestionCriterion();
        qc.setWeight(request.getWeight());
        return toResponse(repository.save(qc));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionCriterionResponse> list() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionCriterionResponse getById(UUID id) {
        QuestionCriterion qc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionCriterion " + id + " not found"));
        return toResponse(qc);
    }

    @Override
    public QuestionCriterionResponse update(UUID id, UpdateQuestionCriterionRequest request) {
        QuestionCriterion qc = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionCriterion " + id + " not found"));
        if (request.getWeight() != null) qc.setWeight(request.getWeight());
        return toResponse(repository.save(qc));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("QuestionCriterion " + id + " not found");
        }
        repository.deleteById(id);
    }

    private QuestionCriterionResponse toResponse(QuestionCriterion qc) {
        QuestionCriterionResponse r = new QuestionCriterionResponse();
        r.setId(qc.getId());
        r.setWeight(qc.getWeight());
        return r;
    }
}
