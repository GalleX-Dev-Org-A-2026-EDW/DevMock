package com.devmock.backend.service.impl;

import com.devmock.backend.dto.AchievementResponse;
import com.devmock.backend.dto.CreateAchievementRequest;
import com.devmock.backend.dto.UpdateAchievementRequest;
import com.devmock.backend.entity.Achievement;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.AchievementRepository;
import com.devmock.backend.service.AchievementService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository repository;

    public AchievementServiceImpl(AchievementRepository repository) {
        this.repository = repository;
    }

    @Override
    public AchievementResponse create(CreateAchievementRequest request) {

        Achievement a = new Achievement();

        a.setName(request.getName());
        a.setSlug(request.getSlug());
        a.setDescription(request.getDescription());
        a.setIconUrl(request.getIconUrl());
        a.setUnlockCriteria(request.getUnlockCriteria());
        a.setPointsReward(request.getPointsReward());
        a.setIsActive(request.getIsActive());

        Achievement saved = repository.save(a);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponse> list() {

        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AchievementResponse getById(UUID id) {

        Achievement a = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Achievement " + id + " not found"));

        return toResponse(a);
    }

    @Override
    public AchievementResponse update(
            UUID id,
            UpdateAchievementRequest request) {

        Achievement a = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Achievement " + id + " not found"));

        if (request.getName() != null)
            a.setName(request.getName());

        if (request.getSlug() != null)
            a.setSlug(request.getSlug());

        if (request.getDescription() != null)
            a.setDescription(request.getDescription());

        if (request.getIconUrl() != null)
            a.setIconUrl(request.getIconUrl());

        if (request.getUnlockCriteria() != null)
            a.setUnlockCriteria(request.getUnlockCriteria());

        if (request.getPointsReward() != null)
            a.setPointsReward(request.getPointsReward());

        if (request.getIsActive() != null)
            a.setIsActive(request.getIsActive());

        return toResponse(repository.save(a));
    }

    @Override
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Achievement " + id + " not found");
        }

        repository.deleteById(id);
    }

    // Mapper

    private AchievementResponse toResponse(Achievement a) {

        AchievementResponse response = new AchievementResponse();

        response.setId(a.getId());
        response.setName(a.getName());
        response.setSlug(a.getSlug());
        response.setDescription(a.getDescription());
        response.setIconUrl(a.getIconUrl());
        response.setUnlockCriteria(a.getUnlockCriteria());
        response.setPointsReward(a.getPointsReward());
        response.setIsActive(a.getIsActive());
        response.setCreatedAt(a.getCreatedAt());

        return response;
    }
}