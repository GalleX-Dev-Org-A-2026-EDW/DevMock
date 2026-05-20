package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.CreateUserAchievementRequest;
import com.devmock.backend.dto.UpdateUserAchievementRequest;
import com.devmock.backend.dto.UserAchievementResponse;

public interface UserAchievementService {

    UserAchievementResponse create(CreateUserAchievementRequest request);

    List<UserAchievementResponse> list();

    UserAchievementResponse getById(UUID id);

    UserAchievementResponse update(UUID id, UpdateUserAchievementRequest request);

    void delete(UUID id);
}