package com.devmock.backend.controller;

import com.devmock.backend.dto.AchievementResponse;
import com.devmock.backend.dto.CreateAchievementRequest;
import com.devmock.backend.dto.UpdateAchievementRequest;
import com.devmock.backend.service.AchievementService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService service;

    public AchievementController(AchievementService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AchievementResponse create(
            @Valid @RequestBody CreateAchievementRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<AchievementResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public AchievementResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public AchievementResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAchievementRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}