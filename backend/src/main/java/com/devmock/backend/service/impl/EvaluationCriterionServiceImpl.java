package com.devmock.backend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devmock.backend.dto.CreateEvaluationCriterionRequest;
import com.devmock.backend.dto.EvaluationCriterionResponse;
import com.devmock.backend.dto.UpdateEvaluationCriterionRequest;
import com.devmock.backend.entity.EvaluationCriterion;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.exception.SlugAlreadyExistsException;
import com.devmock.backend.repository.EvaluationCriterionRepository;
import com.devmock.backend.service.EvaluationCriterionService;

@Service
@Transactional
public class EvaluationCriterionServiceImpl implements EvaluationCriterionService {

    private final EvaluationCriterionRepository repository;

    public EvaluationCriterionServiceImpl(EvaluationCriterionRepository repository) {
        this.repository = repository;
    }

    @Override
    public EvaluationCriterionResponse create(CreateEvaluationCriterionRequest request) {
        if (repository.existsBySlug(request.getSlug())) {
            throw new SlugAlreadyExistsException("Slug '" + request.getSlug() + "' is already in use");
        }
        EvaluationCriterion e = new EvaluationCriterion();
        e.setName(request.getName());
        e.setSlug(request.getSlug());
        e.setDescription(request.getDescription());
        e.setDefaultWeight(request.getDefaultWeight());
        return toResponse(repository.save(e));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationCriterionResponse> list() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluationCriterionResponse> listActive() {
        return repository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationCriterionResponse getById(UUID id) {
        EvaluationCriterion e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationCriterion " + id + " not found"));
        return toResponse(e);
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationCriterionResponse getBySlug(String slug) {
        EvaluationCriterion e = repository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationCriterion with slug '" + slug + "' not found"));
        return toResponse(e);
    }

    @Override
    public EvaluationCriterionResponse update(UUID id, UpdateEvaluationCriterionRequest request) {
        EvaluationCriterion e = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EvaluationCriterion " + id + " not found"));

        if (request.getName() != null) e.setName(request.getName());
        if (request.getSlug() != null && !request.getSlug().equals(e.getSlug())) {
            if (repository.existsBySlug(request.getSlug())) {
                throw new SlugAlreadyExistsException("Slug '" + request.getSlug() + "' is already in use");
            }
            e.setSlug(request.getSlug());
        }
        if (request.getDescription() != null) e.setDescription(request.getDescription());
        if (request.getDefaultWeight() != null) e.setDefaultWeight(request.getDefaultWeight());
        if (request.getIsActive() != null) e.setIsActive(request.getIsActive());

        return toResponse(repository.save(e));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("EvaluationCriterion " + id + " not found");
        }
        repository.deleteById(id);
    }

    private EvaluationCriterionResponse toResponse(EvaluationCriterion e) {
        EvaluationCriterionResponse r = new EvaluationCriterionResponse();
        r.setId(e.getId());
        r.setName(e.getName());
        r.setSlug(e.getSlug());
        r.setDescription(e.getDescription());
        r.setDefaultWeight(e.getDefaultWeight());
        r.setIsActive(e.getIsActive());
        r.setCreatedAt(e.getCreatedAt());
        return r;
    }
}
