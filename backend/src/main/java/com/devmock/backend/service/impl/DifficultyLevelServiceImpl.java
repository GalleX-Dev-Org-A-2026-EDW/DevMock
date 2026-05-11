package com.devmock.backend.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devmock.backend.dto.CreateDifficultyLevelRequest;
import com.devmock.backend.dto.DifficultyLevelResponse;
import com.devmock.backend.dto.UpdateDifficultyLevelRequest;
import com.devmock.backend.entity.DifficultyLevel;
import com.devmock.backend.exception.LevelOrderAlreadyExistsException;
import com.devmock.backend.exception.NameAlreadyExistsException;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.exception.SlugAlreadyExistsException;
import com.devmock.backend.repository.DifficultyLevelRepository;
import com.devmock.backend.service.DifficultyLevelService;

@Service
@Transactional
public class DifficultyLevelServiceImpl implements DifficultyLevelService {

    private final DifficultyLevelRepository repository;

    public DifficultyLevelServiceImpl(DifficultyLevelRepository repository) {
        this.repository = repository;
    }

    @Override
    public DifficultyLevelResponse create(CreateDifficultyLevelRequest request) {
        if (repository.existsByName(request.getName())) {
            throw new NameAlreadyExistsException("Difficulty name '" + request.getName() + "' is already in use");
        }
        if (repository.existsBySlug(request.getSlug())) {
            throw new SlugAlreadyExistsException("Slug '" + request.getSlug() + "' is already in use");
        }
        if (repository.existsByLevelOrder(request.getLevelOrder())) {
            throw new LevelOrderAlreadyExistsException("Level order " + request.getLevelOrder() + " is already in use");
        }

        DifficultyLevel d = new DifficultyLevel();
        d.setName(request.getName());
        d.setSlug(request.getSlug());
        d.setLevelOrder(request.getLevelOrder());
        d.setPointsMultiplier(request.getPointsMultiplier());
        d.setDescription(request.getDescription());

        DifficultyLevel saved = repository.save(d);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DifficultyLevelResponse> list() {
        return repository.findAllByOrderByLevelOrderAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DifficultyLevelResponse getById(UUID id) {
        DifficultyLevel d = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DifficultyLevel " + id + " not found"));
        return toResponse(d);
    }

    @Override
    @Transactional(readOnly = true)
    public DifficultyLevelResponse getBySlug(String slug) {
        DifficultyLevel d = repository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("DifficultyLevel with slug '" + slug + "' not found"));
        return toResponse(d);
    }

    @Override
    public DifficultyLevelResponse update(UUID id, UpdateDifficultyLevelRequest request) {
        DifficultyLevel d = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DifficultyLevel " + id + " not found"));

        if (request.getName() != null && !request.getName().equals(d.getName())) {
            if (repository.existsByName(request.getName())) {
                throw new NameAlreadyExistsException("Difficulty name '" + request.getName() + "' is already in use");
            }
            d.setName(request.getName());
        }

        if (request.getSlug() != null && !request.getSlug().equals(d.getSlug())) {
            if (repository.existsBySlug(request.getSlug())) {
                throw new SlugAlreadyExistsException("Slug '" + request.getSlug() + "' is already in use");
            }
            d.setSlug(request.getSlug());
        }

        if (request.getLevelOrder() != null && !request.getLevelOrder().equals(d.getLevelOrder())) {
            if (repository.existsByLevelOrder(request.getLevelOrder())) {
                throw new LevelOrderAlreadyExistsException("Level order " + request.getLevelOrder() + " is already in use");
            }
            d.setLevelOrder(request.getLevelOrder());
        }

        if (request.getPointsMultiplier() != null) d.setPointsMultiplier(request.getPointsMultiplier());
        if (request.getDescription() != null) d.setDescription(request.getDescription());

        return toResponse(repository.save(d));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("DifficultyLevel " + id + " not found");
        }
        repository.deleteById(id);
    }

    // Mapper
    private DifficultyLevelResponse toResponse(DifficultyLevel d) {
        DifficultyLevelResponse r = new DifficultyLevelResponse();
        r.setId(d.getId());
        r.setName(d.getName());
        r.setSlug(d.getSlug());
        r.setLevelOrder(d.getLevelOrder());
        r.setPointsMultiplier(d.getPointsMultiplier());
        r.setDescription(d.getDescription());
        r.setCreatedAt(d.getCreatedAt());
        return r;
    }
}
