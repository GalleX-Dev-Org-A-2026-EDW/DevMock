package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.AnswerOptionResponse;
import com.devmock.backend.dto.CreateAnswerOptionRequest;
import com.devmock.backend.dto.UpdateAnswerOptionRequest;

public interface AnswerOptionService {

    AnswerOptionResponse create(CreateAnswerOptionRequest request);

    List<AnswerOptionResponse> list();

    AnswerOptionResponse getById(UUID id);

    AnswerOptionResponse update(UUID id, UpdateAnswerOptionRequest request);

    void delete(UUID id);
}
