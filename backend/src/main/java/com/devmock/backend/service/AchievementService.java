package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.AchievementResponse;
import com.devmock.backend.dto.CreateAchievementRequest;
import com.devmock.backend.dto.UpdateAchievementRequest;

public interface AchievementService {

    AchievementResponse create(CreateAchievementRequest request);

    List<AchievementResponse> list();

    AchievementResponse getById(UUID id);

    AchievementResponse update(UUID id, UpdateAchievementRequest request);

    void delete(UUID id);
}