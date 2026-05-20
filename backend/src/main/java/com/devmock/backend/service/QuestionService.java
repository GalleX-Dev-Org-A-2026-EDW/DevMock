package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.CreateQuestionRequest;
import com.devmock.backend.dto.QuestionResponse;
import com.devmock.backend.dto.UpdateQuestionRequest;

public interface QuestionService {

    QuestionResponse create(CreateQuestionRequest request);

    List<QuestionResponse> list();

    QuestionResponse getById(UUID id);

    QuestionResponse update(UUID id, UpdateQuestionRequest request);

    void delete(UUID id);
}
