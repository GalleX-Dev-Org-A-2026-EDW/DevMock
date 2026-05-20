package com.devmock.backend.service.impl;

import com.devmock.backend.dto.CreateRankingRequest;
import com.devmock.backend.dto.UpdateRankingRequest;
import com.devmock.backend.dto.RankingResponse;
import com.devmock.backend.entity.Ranking;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.RankingRepository;
import com.devmock.backend.service.RankingService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RankingServiceImpl implements RankingService {

    private final RankingRepository repository;

    public RankingServiceImpl(RankingRepository repository) {
        this.repository = repository;
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
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ranking " + id + " not found"));

        return toResponse(r);
    }

    @Override
    public RankingResponse update(
            UUID id,
            UpdateRankingRequest request) {

        Ranking r = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ranking " + id + " not found"));

        if (request.getPeriod() != null)
            r.setPeriod(request.getPeriod());

        if (request.getPeriodStartDate() != null)
            r.setPeriodStartDate(request.getPeriodStartDate());

        if (request.getPeriodEndDate() != null)
            r.setPeriodEndDate(request.getPeriodEndDate());

        if (request.getTotalScore() != null)
            r.setTotalScore(request.getTotalScore());

        if (request.getTotalSessions() != null)
            r.setTotalSessions(request.getTotalSessions());

        if (request.getRankPosition() != null)
            r.setRankPosition(request.getRankPosition());

        if (request.getCalculatedAt() != null)
            r.setCalculatedAt(request.getCalculatedAt());

        return toResponse(repository.save(r));
    }

    @Override
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Ranking " + id + " not found");
        }

        repository.deleteById(id);
    }

    // Mapper

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

        return response;
    }
}