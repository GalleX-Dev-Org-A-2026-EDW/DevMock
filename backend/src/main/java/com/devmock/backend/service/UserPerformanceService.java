package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.CreateUserPerformanceRequest;
import com.devmock.backend.dto.UpdateUserPerformanceRequest;
import com.devmock.backend.dto.UserPerformanceResponse;

public interface UserPerformanceService {

    UserPerformanceResponse create(CreateUserPerformanceRequest request);

    List<UserPerformanceResponse> list();

    UserPerformanceResponse getById(UUID id);

    UserPerformanceResponse update(UUID id, UpdateUserPerformanceRequest request);

    void delete(UUID id);
}