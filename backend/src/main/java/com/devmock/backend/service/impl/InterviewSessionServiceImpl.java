package com.devmock.backend.service.impl;

import com.devmock.backend.dto.CreateInterviewSessionRequest;
import com.devmock.backend.dto.UpdateInterviewSessionRequest;
import com.devmock.backend.dto.InterviewSessionResponse;
import com.devmock.backend.entity.Category;
import com.devmock.backend.entity.DifficultyLevel;
import com.devmock.backend.entity.InterviewSession;
import com.devmock.backend.entity.InterviewType;
import com.devmock.backend.entity.User;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.CategoryRepository;
import com.devmock.backend.repository.DifficultyLevelRepository;
import com.devmock.backend.repository.InterviewSessionRepository;
import com.devmock.backend.repository.InterviewTypeRepository;
import com.devmock.backend.repository.UserRepository;
import com.devmock.backend.entity.en_enum.SessionStatus;
import com.devmock.backend.security.SecurityUtils;
import com.devmock.backend.service.InterviewSessionService;
import com.devmock.backend.service.RankingService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InterviewSessionServiceImpl implements InterviewSessionService {

    private final InterviewSessionRepository repository;
    private final UserRepository userRepository;
    private final InterviewTypeRepository interviewTypeRepository;
    private final DifficultyLevelRepository difficultyLevelRepository;
    private final CategoryRepository categoryRepository;
    private final RankingService rankingService;
    private final SecurityUtils securityUtils;

    public InterviewSessionServiceImpl(InterviewSessionRepository repository,
            UserRepository userRepository,
            InterviewTypeRepository interviewTypeRepository,
            DifficultyLevelRepository difficultyLevelRepository,
            CategoryRepository categoryRepository,
            RankingService rankingService,
            SecurityUtils securityUtils) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.interviewTypeRepository = interviewTypeRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
        this.categoryRepository = categoryRepository;
        this.rankingService = rankingService;
        this.securityUtils = securityUtils;
    }

    @Override
    public InterviewSessionResponse create(CreateInterviewSessionRequest request) {
        InterviewSession s = new InterviewSession();

        s.setStatus(request.getStatus());
        s.setStartedAt(request.getStartedAt());
        s.setFinishedAt(request.getFinishedAt());
        s.setTotalTimeUsedSeconds(request.getTotalTimeUsedSeconds());
        s.setFinalScore(request.getFinalScore());
        s.setCorrectnessScore(request.getCorrectnessScore());
        s.setEfficiencyScore(request.getEfficiencyScore());
        s.setLogicScore(request.getLogicScore());
        s.setClarityScore(request.getClarityScore());

        s.setUser(securityUtils.getCurrentUser());
        if (request.getInterviewTypeId() != null) {
            InterviewType type = interviewTypeRepository.findById(request.getInterviewTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "InterviewType " + request.getInterviewTypeId() + " not found"));
            s.setInterviewType(type);
        }
        if (request.getDifficultyId() != null) {
            DifficultyLevel level = difficultyLevelRepository.findById(request.getDifficultyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DifficultyLevel " + request.getDifficultyId() + " not found"));
            s.setDifficulty(level);
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category " + request.getCategoryId() + " not found"));
            s.setCategory(category);
        }

        InterviewSession saved = repository.save(s);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewSessionResponse> list() {
        return repository.findByUser(securityUtils.getCurrentUser())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewSessionResponse getById(UUID id) {
        InterviewSession s = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session " + id + " not found"));
        return toResponse(s);
    }

    @Override
    public InterviewSessionResponse update(UUID id, UpdateInterviewSessionRequest request) {
        InterviewSession s = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session " + id + " not found"));

        if (request.getStatus() != null) s.setStatus(request.getStatus());
        if (request.getStartedAt() != null) s.setStartedAt(request.getStartedAt());
        if (request.getFinishedAt() != null) s.setFinishedAt(request.getFinishedAt());
        if (request.getTotalTimeUsedSeconds() != null) s.setTotalTimeUsedSeconds(request.getTotalTimeUsedSeconds());
        if (request.getFinalScore() != null) s.setFinalScore(request.getFinalScore());
        if (request.getCorrectnessScore() != null) s.setCorrectnessScore(request.getCorrectnessScore());
        if (request.getEfficiencyScore() != null) s.setEfficiencyScore(request.getEfficiencyScore());
        if (request.getLogicScore() != null) s.setLogicScore(request.getLogicScore());
        if (request.getClarityScore() != null) s.setClarityScore(request.getClarityScore());

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User " + request.getUserId() + " not found"));
            s.setUser(user);
        }
        if (request.getInterviewTypeId() != null) {
            InterviewType type = interviewTypeRepository.findById(request.getInterviewTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "InterviewType " + request.getInterviewTypeId() + " not found"));
            s.setInterviewType(type);
        }
        if (request.getDifficultyId() != null) {
            DifficultyLevel level = difficultyLevelRepository.findById(request.getDifficultyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DifficultyLevel " + request.getDifficultyId() + " not found"));
            s.setDifficulty(level);
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category " + request.getCategoryId() + " not found"));
            s.setCategory(category);
        }

        InterviewSession saved = repository.save(s);
        if (SessionStatus.COMPLETED.equals(request.getStatus())) {
            rankingService.recalculate();
        }
        return toResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Session " + id + " not found");
        }
        repository.deleteById(id);
    }

    private InterviewSessionResponse toResponse(InterviewSession s) {
        InterviewSessionResponse r = new InterviewSessionResponse();

        r.setId(s.getId());
        r.setStatus(s.getStatus());
        r.setStartedAt(s.getStartedAt());
        r.setFinishedAt(s.getFinishedAt());
        r.setTotalTimeUsedSeconds(s.getTotalTimeUsedSeconds());
        r.setFinalScore(s.getFinalScore());
        r.setCorrectnessScore(s.getCorrectnessScore());
        r.setEfficiencyScore(s.getEfficiencyScore());
        r.setLogicScore(s.getLogicScore());
        r.setClarityScore(s.getClarityScore());

        if (s.getUser() != null) r.setUserId(s.getUser().getId());
        if (s.getInterviewType() != null) r.setInterviewTypeId(s.getInterviewType().getId());
        if (s.getDifficulty() != null) r.setDifficultyId(s.getDifficulty().getId());
        if (s.getCategory() != null) r.setCategoryId(s.getCategory().getId());

        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());

        return r;
    }
}
