package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.CreateEvaluationCriterionRequest;
import com.devmock.backend.dto.EvaluationCriterionResponse;
import com.devmock.backend.dto.UpdateEvaluationCriterionRequest;

public interface EvaluationCriterionService {

    EvaluationCriterionResponse create(CreateEvaluationCriterionRequest request);

    List<EvaluationCriterionResponse> list();

    List<EvaluationCriterionResponse> listActive();

    EvaluationCriterionResponse getById(UUID id);

    EvaluationCriterionResponse getBySlug(String slug);

    EvaluationCriterionResponse update(UUID id, UpdateEvaluationCriterionRequest request);

    void delete(UUID id);
}
