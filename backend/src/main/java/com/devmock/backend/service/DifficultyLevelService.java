package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.CreateDifficultyLevelRequest;
import com.devmock.backend.dto.DifficultyLevelResponse;
import com.devmock.backend.dto.UpdateDifficultyLevelRequest;

public interface DifficultyLevelService {

    DifficultyLevelResponse create(CreateDifficultyLevelRequest request);

    List<DifficultyLevelResponse> list();

    DifficultyLevelResponse getById(UUID id);

    DifficultyLevelResponse getBySlug(String slug);

    DifficultyLevelResponse update(UUID id, UpdateDifficultyLevelRequest request);

    void delete(UUID id);
}
