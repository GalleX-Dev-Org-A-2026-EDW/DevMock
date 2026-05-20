package com.devmock.backend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devmock.backend.dto.AnswerOptionResponse;
import com.devmock.backend.dto.CreateAnswerOptionRequest;
import com.devmock.backend.dto.UpdateAnswerOptionRequest;
import com.devmock.backend.entity.AnswerOption;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.AnswerOptionRepository;
import com.devmock.backend.service.AnswerOptionService;

@Service
@Transactional
public class AnswerOptionServiceImpl implements AnswerOptionService {

    private final AnswerOptionRepository repository;

    public AnswerOptionServiceImpl(AnswerOptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public AnswerOptionResponse create(CreateAnswerOptionRequest request) {
        AnswerOption a = new AnswerOption();
        a.setOptionText(request.getOptionText());
        a.setIsCorrect(request.getIsCorrect());
        a.setExplanation(request.getExplanation());
        a.setDisplayOrder(request.getDisplayOrder());
        return toResponse(repository.save(a));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnswerOptionResponse> list() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerOptionResponse getById(UUID id) {
        AnswerOption a = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnswerOption " + id + " not found"));
        return toResponse(a);
    }

    @Override
    public AnswerOptionResponse update(UUID id, UpdateAnswerOptionRequest request) {
        AnswerOption a = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AnswerOption " + id + " not found"));
        if (request.getOptionText() != null) a.setOptionText(request.getOptionText());
        if (request.getIsCorrect() != null) a.setIsCorrect(request.getIsCorrect());
        if (request.getExplanation() != null) a.setExplanation(request.getExplanation());
        if (request.getDisplayOrder() != null) a.setDisplayOrder(request.getDisplayOrder());
        return toResponse(repository.save(a));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("AnswerOption " + id + " not found");
        }
        repository.deleteById(id);
    }

    private AnswerOptionResponse toResponse(AnswerOption a) {
        AnswerOptionResponse r = new AnswerOptionResponse();
        r.setId(a.getId());
        r.setOptionText(a.getOptionText());
        r.setIsCorrect(a.getIsCorrect());
        r.setExplanation(a.getExplanation());
        r.setDisplayOrder(a.getDisplayOrder());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }
}
