package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.CreateQuestionCriterionRequest;
import com.devmock.backend.dto.QuestionCriterionResponse;
import com.devmock.backend.dto.UpdateQuestionCriterionRequest;

public interface QuestionCriterionService {

    QuestionCriterionResponse create(CreateQuestionCriterionRequest request);

    List<QuestionCriterionResponse> list();

    QuestionCriterionResponse getById(UUID id);

    QuestionCriterionResponse update(UUID id, UpdateQuestionCriterionRequest request);

    void delete(UUID id);
}
