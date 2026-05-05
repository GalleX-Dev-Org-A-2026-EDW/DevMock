package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.CreateInterviewSessionRequest;
import com.devmock.backend.dto.UpdateInterviewSessionRequest;
import com.devmock.backend.dto.InterviewSessionResponse;

public interface InterviewSessionService {

    InterviewSessionResponse create(CreateInterviewSessionRequest request);

    List<InterviewSessionResponse> list();

    InterviewSessionResponse getById(UUID id);

    InterviewSessionResponse update(UUID id, UpdateInterviewSessionRequest request);

    void delete(UUID id);
}