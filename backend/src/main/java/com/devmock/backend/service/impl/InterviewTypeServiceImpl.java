package com.devmock.backend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devmock.backend.dto.CreateInterviewTypeRequest;
import com.devmock.backend.dto.InterviewTypeResponse;
import com.devmock.backend.dto.UpdateInterviewTypeRequest;
import com.devmock.backend.entity.InterviewType;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.exception.SlugAlreadyExistsException;
import com.devmock.backend.repository.InterviewTypeRepository;
import com.devmock.backend.service.InterviewTypeService;

@Service
@Transactional
public class InterviewTypeServiceImpl implements InterviewTypeService {

    private final InterviewTypeRepository repository;

    public InterviewTypeServiceImpl(InterviewTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public InterviewTypeResponse create(CreateInterviewTypeRequest request) {
        if (repository.existsBySlug(request.getSlug())) {
            throw new SlugAlreadyExistsException("Slug '" + request.getSlug() + "' is already in use");
        }
        InterviewType it = new InterviewType();
        it.setName(request.getName());
        it.setSlug(request.getSlug());
        it.setQuestionType(request.getQuestionType());
        it.setTotalQuestions(request.getTotalQuestions());
        it.setTotalTimeSeconds(request.getTotalTimeSeconds());
        it.setDescription(request.getDescription());
        return toResponse(repository.save(it));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewTypeResponse> list() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewTypeResponse> listActive() {
        return repository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewTypeResponse getById(UUID id) {
        InterviewType it = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewType " + id + " not found"));
        return toResponse(it);
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewTypeResponse getBySlug(String slug) {
        InterviewType it = repository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewType with slug '" + slug + "' not found"));
        return toResponse(it);
    }

    @Override
    public InterviewTypeResponse update(UUID id, UpdateInterviewTypeRequest request) {
        InterviewType it = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewType " + id + " not found"));

        if (request.getName() != null) it.setName(request.getName());
        if (request.getSlug() != null && !request.getSlug().equals(it.getSlug())) {
            if (repository.existsBySlug(request.getSlug())) {
                throw new SlugAlreadyExistsException("Slug '" + request.getSlug() + "' is already in use");
            }
            it.setSlug(request.getSlug());
        }
        if (request.getQuestionType() != null) it.setQuestionType(request.getQuestionType());
        if (request.getTotalQuestions() != null) it.setTotalQuestions(request.getTotalQuestions());
        if (request.getTotalTimeSeconds() != null) it.setTotalTimeSeconds(request.getTotalTimeSeconds());
        if (request.getDescription() != null) it.setDescription(request.getDescription());
        if (request.getIsActive() != null) it.setIsActive(request.getIsActive());

        return toResponse(repository.save(it));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("InterviewType " + id + " not found");
        }
        repository.deleteById(id);
    }

    private InterviewTypeResponse toResponse(InterviewType it) {
        InterviewTypeResponse r = new InterviewTypeResponse();
        r.setId(it.getId());
        r.setName(it.getName());
        r.setSlug(it.getSlug());
        r.setQuestionType(it.getQuestionType());
        r.setTotalQuestions(it.getTotalQuestions());
        r.setTotalTimeSeconds(it.getTotalTimeSeconds());
        r.setDescription(it.getDescription());
        r.setIsActive(it.getIsActive());
        r.setCreatedAt(it.getCreatedAt());
        return r;
    }
}
