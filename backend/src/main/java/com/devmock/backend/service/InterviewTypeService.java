package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.CreateInterviewTypeRequest;
import com.devmock.backend.dto.InterviewTypeResponse;
import com.devmock.backend.dto.UpdateInterviewTypeRequest;

public interface InterviewTypeService {

    InterviewTypeResponse create(CreateInterviewTypeRequest request);

    List<InterviewTypeResponse> list();

    List<InterviewTypeResponse> listActive();

    InterviewTypeResponse getById(UUID id);

    InterviewTypeResponse getBySlug(String slug);

    InterviewTypeResponse update(UUID id, UpdateInterviewTypeRequest request);

    void delete(UUID id);
}
