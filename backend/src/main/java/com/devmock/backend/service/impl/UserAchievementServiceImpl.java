package com.devmock.backend.service.impl;

import com.devmock.backend.dto.CreateUserAchievementRequest;
import com.devmock.backend.dto.UpdateUserAchievementRequest;
import com.devmock.backend.dto.UserAchievementResponse;
import com.devmock.backend.entity.UserAchievement;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.UserAchievementRepository;
import com.devmock.backend.service.UserAchievementService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserAchievementServiceImpl implements UserAchievementService {

    private final UserAchievementRepository repository;

    public UserAchievementServiceImpl(UserAchievementRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserAchievementResponse create(CreateUserAchievementRequest request) {

        UserAchievement ua = new UserAchievement();

        ua.setUnlockedAt(request.getUnlockedAt());
        ua.setIsViewed(request.getIsViewed());

        UserAchievement saved = repository.save(ua);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAchievementResponse> list() {

        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserAchievementResponse getById(UUID id) {

        UserAchievement ua = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "UserAchievement " + id + " not found"));

        return toResponse(ua);
    }

    @Override
    public UserAchievementResponse update(
            UUID id,
            UpdateUserAchievementRequest request) {

        UserAchievement ua = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "UserAchievement " + id + " not found"));

        if (request.getUnlockedAt() != null)
            ua.setUnlockedAt(request.getUnlockedAt());

        if (request.getIsViewed() != null)
            ua.setIsViewed(request.getIsViewed());

        return toResponse(repository.save(ua));
    }

    @Override
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "UserAchievement " + id + " not found");
        }

        repository.deleteById(id);
    }

    // Mapper

    private UserAchievementResponse toResponse(UserAchievement ua) {

        UserAchievementResponse response = new UserAchievementResponse();

        response.setId(ua.getId());
        response.setUnlockedAt(ua.getUnlockedAt());
        response.setIsViewed(ua.getIsViewed());
        response.setCreatedAt(ua.getCreatedAt());

        return response;
    }
}