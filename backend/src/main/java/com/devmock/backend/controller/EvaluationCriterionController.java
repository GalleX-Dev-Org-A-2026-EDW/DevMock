package com.devmock.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devmock.backend.dto.CreateEvaluationCriterionRequest;
import com.devmock.backend.dto.EvaluationCriterionResponse;
import com.devmock.backend.dto.UpdateEvaluationCriterionRequest;
import com.devmock.backend.service.EvaluationCriterionService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/evaluation-criteria")
public class EvaluationCriterionController {

    private final EvaluationCriterionService service;

    public EvaluationCriterionController(EvaluationCriterionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public EvaluationCriterionResponse create(@Valid @RequestBody CreateEvaluationCriterionRequest request) {
        return service.create(request);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<EvaluationCriterionResponse> list(
            @RequestParam(name = "activeOnly", required = false, defaultValue = "false") boolean activeOnly) {
        return activeOnly ? service.listActive() : service.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public EvaluationCriterionResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/by-slug/{slug}")
    @PreAuthorize("isAuthenticated()")
    public EvaluationCriterionResponse getBySlug(@PathVariable String slug) {
        return service.getBySlug(slug);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EvaluationCriterionResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEvaluationCriterionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
