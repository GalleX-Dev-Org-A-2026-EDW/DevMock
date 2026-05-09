package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.CreateSessionQuestionRequest;
import com.devmock.backend.dto.UpdateSessionQuestionRequest;
import com.devmock.backend.dto.SessionQuestionResponse;

public interface SessionQuestionService {

    SessionQuestionResponse create(CreateSessionQuestionRequest request);

    List<SessionQuestionResponse> list();

    SessionQuestionResponse getById(UUID id);

    SessionQuestionResponse update(UUID id, UpdateSessionQuestionRequest request);

    void delete(UUID id);
}