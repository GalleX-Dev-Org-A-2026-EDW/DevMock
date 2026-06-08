package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.AchievementResponse;
import com.devmock.backend.dto.CreateUserAchievementRequest;
import com.devmock.backend.dto.UpdateUserAchievementRequest;
import com.devmock.backend.dto.UserAchievementResponse;
import com.devmock.backend.entity.SessionQuestion;
import com.devmock.backend.entity.User;

public interface UserAchievementService {

    UserAchievementResponse create(CreateUserAchievementRequest request);

    List<UserAchievementResponse> list();

    UserAchievementResponse getById(UUID id);

    UserAchievementResponse update(UUID id, UpdateUserAchievementRequest request);

    void delete(UUID id);

    List<AchievementResponse> checkAndUnlockOnSessionCompleted(User user);

    List<AchievementResponse> checkAndUnlockOnQuestionEvaluated(User user, SessionQuestion sq);
}