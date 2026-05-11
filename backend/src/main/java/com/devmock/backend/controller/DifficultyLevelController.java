package com.devmock.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.devmock.backend.dto.CreateDifficultyLevelRequest;
import com.devmock.backend.dto.DifficultyLevelResponse;
import com.devmock.backend.dto.UpdateDifficultyLevelRequest;
import com.devmock.backend.service.DifficultyLevelService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/difficulty-levels")
public class DifficultyLevelController {

    private final DifficultyLevelService service;

    public DifficultyLevelController(DifficultyLevelService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DifficultyLevelResponse create(@Valid @RequestBody CreateDifficultyLevelRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<DifficultyLevelResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public DifficultyLevelResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/by-slug/{slug}")
    public DifficultyLevelResponse getBySlug(@PathVariable String slug) {
        return service.getBySlug(slug);
    }

    @PutMapping("/{id}")
    public DifficultyLevelResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDifficultyLevelRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
