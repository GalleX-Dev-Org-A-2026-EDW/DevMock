package com.devmock.backend.service.impl;

import com.devmock.backend.dto.CreateRankingRequest;
import com.devmock.backend.dto.UpdateRankingRequest;
import com.devmock.backend.dto.RankingResponse;
import com.devmock.backend.entity.Category;
import com.devmock.backend.entity.DifficultyLevel;
import com.devmock.backend.entity.Ranking;
import com.devmock.backend.entity.User;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.CategoryRepository;
import com.devmock.backend.repository.DifficultyLevelRepository;
import com.devmock.backend.repository.RankingRepository;
import com.devmock.backend.repository.UserRepository;
import com.devmock.backend.service.RankingService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RankingServiceImpl implements RankingService {

    private final RankingRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DifficultyLevelRepository difficultyLevelRepository;

    public RankingServiceImpl(RankingRepository repository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            DifficultyLevelRepository difficultyLevelRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
    }

    @Override
    public RankingResponse create(CreateRankingRequest request) {
        Ranking r = new Ranking();

        r.setPeriod(request.getPeriod());
        r.setPeriodStartDate(request.getPeriodStartDate());
        r.setPeriodEndDate(request.getPeriodEndDate());
        r.setTotalScore(request.getTotalScore());
        r.setTotalSessions(request.getTotalSessions());
        r.setRankPosition(request.getRankPosition());
        r.setCalculatedAt(request.getCalculatedAt());

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User " + request.getUserId() + " not found"));
            r.setUser(user);
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category " + request.getCategoryId() + " not found"));
            r.setCategory(category);
        }
        if (request.getDifficultyId() != null) {
            DifficultyLevel level = difficultyLevelRepository.findById(request.getDifficultyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DifficultyLevel " + request.getDifficultyId() + " not found"));
            r.setDifficulty(level);
        }

        Ranking saved = repository.save(r);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankingResponse> list() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RankingResponse getById(UUID id) {
        Ranking r = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ranking " + id + " not found"));
        return toResponse(r);
    }

    @Override
    public RankingResponse update(UUID id, UpdateRankingRequest request) {
        Ranking r = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ranking " + id + " not found"));

        if (request.getPeriod() != null) r.setPeriod(request.getPeriod());
        if (request.getPeriodStartDate() != null) r.setPeriodStartDate(request.getPeriodStartDate());
        if (request.getPeriodEndDate() != null) r.setPeriodEndDate(request.getPeriodEndDate());
        if (request.getTotalScore() != null) r.setTotalScore(request.getTotalScore());
        if (request.getTotalSessions() != null) r.setTotalSessions(request.getTotalSessions());
        if (request.getRankPosition() != null) r.setRankPosition(request.getRankPosition());
        if (request.getCalculatedAt() != null) r.setCalculatedAt(request.getCalculatedAt());

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User " + request.getUserId() + " not found"));
            r.setUser(user);
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category " + request.getCategoryId() + " not found"));
            r.setCategory(category);
        }
        if (request.getDifficultyId() != null) {
            DifficultyLevel level = difficultyLevelRepository.findById(request.getDifficultyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DifficultyLevel " + request.getDifficultyId() + " not found"));
            r.setDifficulty(level);
        }

        return toResponse(repository.save(r));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Ranking " + id + " not found");
        }
        repository.deleteById(id);
    }

    private RankingResponse toResponse(Ranking r) {
        RankingResponse response = new RankingResponse();

        response.setId(r.getId());
        response.setPeriod(r.getPeriod());
        response.setPeriodStartDate(r.getPeriodStartDate());
        response.setPeriodEndDate(r.getPeriodEndDate());
        response.setTotalScore(r.getTotalScore());
        response.setTotalSessions(r.getTotalSessions());
        response.setRankPosition(r.getRankPosition());
        response.setCalculatedAt(r.getCalculatedAt());

        if (r.getUser() != null) response.setUserId(r.getUser().getId());
        if (r.getCategory() != null) response.setCategoryId(r.getCategory().getId());
        if (r.getDifficulty() != null) response.setDifficultyId(r.getDifficulty().getId());

        return response;
    }
}
