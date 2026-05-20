package com.devmock.backend.service.impl;

import com.devmock.backend.dto.CreateUserPerformanceRequest;
import com.devmock.backend.dto.UpdateUserPerformanceRequest;
import com.devmock.backend.dto.UserPerformanceResponse;
import com.devmock.backend.entity.UserPerformance;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.UserPerformanceRepository;
import com.devmock.backend.service.UserPerformanceService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserPerformanceServiceImpl implements UserPerformanceService {

    private final UserPerformanceRepository repository;

    public UserPerformanceServiceImpl(UserPerformanceRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserPerformanceResponse create(CreateUserPerformanceRequest request) {

        UserPerformance u = new UserPerformance();

        u.setTotalQuestionsAnswered(request.getTotalQuestionsAnswered());
        u.setTotalCorrect(request.getTotalCorrect());
        u.setAccuracyPercentage(request.getAccuracyPercentage());
        u.setAvgTimeSeconds(request.getAvgTimeSeconds());
        u.setAvgScore(request.getAvgScore());
        u.setStrengths(request.getStrengths());
        u.setWeaknesses(request.getWeaknesses());
        u.setLastPracticedAt(request.getLastPracticedAt());

        UserPerformance saved = repository.save(u);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserPerformanceResponse> list() {

        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserPerformanceResponse getById(UUID id) {

        UserPerformance u = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "UserPerformance " + id + " not found"));

        return toResponse(u);
    }

    @Override
    public UserPerformanceResponse update(
            UUID id,
            UpdateUserPerformanceRequest request) {

        UserPerformance u = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "UserPerformance " + id + " not found"));

        if (request.getTotalQuestionsAnswered() != null)
            u.setTotalQuestionsAnswered(request.getTotalQuestionsAnswered());

        if (request.getTotalCorrect() != null)
            u.setTotalCorrect(request.getTotalCorrect());

        if (request.getAccuracyPercentage() != null)
            u.setAccuracyPercentage(request.getAccuracyPercentage());

        if (request.getAvgTimeSeconds() != null)
            u.setAvgTimeSeconds(request.getAvgTimeSeconds());

        if (request.getAvgScore() != null)
            u.setAvgScore(request.getAvgScore());

        if (request.getStrengths() != null)
            u.setStrengths(request.getStrengths());

        if (request.getWeaknesses() != null)
            u.setWeaknesses(request.getWeaknesses());

        if (request.getLastPracticedAt() != null)
            u.setLastPracticedAt(request.getLastPracticedAt());

        return toResponse(repository.save(u));
    }

    @Override
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "UserPerformance " + id + " not found");
        }

        repository.deleteById(id);
    }

    // Mapper

    private UserPerformanceResponse toResponse(UserPerformance u) {

        UserPerformanceResponse r = new UserPerformanceResponse();

        r.setId(u.getId());
        r.setTotalQuestionsAnswered(u.getTotalQuestionsAnswered());
        r.setTotalCorrect(u.getTotalCorrect());
        r.setAccuracyPercentage(u.getAccuracyPercentage());
        r.setAvgTimeSeconds(u.getAvgTimeSeconds());
        r.setAvgScore(u.getAvgScore());
        r.setStrengths(u.getStrengths());
        r.setWeaknesses(u.getWeaknesses());
        r.setLastPracticedAt(u.getLastPracticedAt());

        r.setUpdatedAt(u.getUpdatedAt());

        return r;
    }
}