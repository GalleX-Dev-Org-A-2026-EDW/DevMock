package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.CreateRankingRequest;
import com.devmock.backend.dto.UpdateRankingRequest;
import com.devmock.backend.dto.RankingResponse;

public interface RankingService {

    RankingResponse create(CreateRankingRequest request);

    List<RankingResponse> list();

    RankingResponse getById(UUID id);

    RankingResponse update(UUID id, UpdateRankingRequest request);

    void delete(UUID id);
}