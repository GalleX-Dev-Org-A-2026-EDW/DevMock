package com.devmock.backend.controller;

import com.devmock.backend.dto.CreateRankingRequest;
import com.devmock.backend.dto.UpdateRankingRequest;
import com.devmock.backend.dto.RankingResponse;
import com.devmock.backend.service.RankingService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rankings")
@PreAuthorize("isAuthenticated()")
public class RankingController {

    private final RankingService service;

    public RankingController(RankingService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RankingResponse create(
            @Valid @RequestBody CreateRankingRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<RankingResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public RankingResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public RankingResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRankingRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}