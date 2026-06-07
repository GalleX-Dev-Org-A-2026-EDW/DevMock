package com.devmock.backend.service.impl;

import com.devmock.backend.dto.CreateUserAchievementRequest;
import com.devmock.backend.dto.UpdateUserAchievementRequest;
import com.devmock.backend.dto.UserAchievementResponse;
import com.devmock.backend.entity.Achievement;
import com.devmock.backend.entity.User;
import com.devmock.backend.entity.UserAchievement;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.AchievementRepository;
import com.devmock.backend.repository.UserAchievementRepository;
import com.devmock.backend.repository.UserRepository;
import com.devmock.backend.security.SecurityUtils;
import com.devmock.backend.service.UserAchievementService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserAchievementServiceImpl implements UserAchievementService {

    private final UserAchievementRepository repository;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final SecurityUtils securityUtils;

    public UserAchievementServiceImpl(UserAchievementRepository repository,
            UserRepository userRepository,
            AchievementRepository achievementRepository,
            SecurityUtils securityUtils) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    public UserAchievementResponse create(CreateUserAchievementRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User " + request.getUserId() + " not found"));
        Achievement achievement = achievementRepository.findById(request.getAchievementId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Achievement " + request.getAchievementId() + " not found"));

        UserAchievement ua = new UserAchievement();
        ua.setUser(user);
        ua.setAchievement(achievement);
        ua.setUnlockedAt(request.getUnlockedAt());
        ua.setIsViewed(request.getIsViewed());

        UserAchievement saved = repository.save(ua);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAchievementResponse> list() {
        return repository.findByUser(securityUtils.getCurrentUser())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserAchievementResponse getById(UUID id) {
        UserAchievement ua = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserAchievement " + id + " not found"));
        return toResponse(ua);
    }

    @Override
    public UserAchievementResponse update(UUID id, UpdateUserAchievementRequest request) {
        UserAchievement ua = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserAchievement " + id + " not found"));

        if (request.getUnlockedAt() != null) ua.setUnlockedAt(request.getUnlockedAt());
        if (request.getIsViewed() != null) ua.setIsViewed(request.getIsViewed());

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User " + request.getUserId() + " not found"));
            ua.setUser(user);
        }
        if (request.getAchievementId() != null) {
            Achievement achievement = achievementRepository.findById(request.getAchievementId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Achievement " + request.getAchievementId() + " not found"));
            ua.setAchievement(achievement);
        }

        return toResponse(repository.save(ua));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("UserAchievement " + id + " not found");
        }
        repository.deleteById(id);
    }

    private UserAchievementResponse toResponse(UserAchievement ua) {
        UserAchievementResponse response = new UserAchievementResponse();

        response.setId(ua.getId());
        if (ua.getUser() != null) response.setUserId(ua.getUser().getId());
        if (ua.getAchievement() != null) response.setAchievementId(ua.getAchievement().getId());
        response.setUnlockedAt(ua.getUnlockedAt());
        response.setIsViewed(ua.getIsViewed());
        response.setCreatedAt(ua.getCreatedAt());

        return response;
    }
}
