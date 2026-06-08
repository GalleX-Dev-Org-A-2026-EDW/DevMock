package com.devmock.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.devmock.backend.dto.CreateDifficultyLevelRequest;
import com.devmock.backend.dto.DifficultyLevelResponse;
import com.devmock.backend.dto.UpdateDifficultyLevelRequest;
import com.devmock.backend.entity.en_enum.AuditAction;
import com.devmock.backend.service.DifficultyLevelService;
import com.devmock.backend.util.AuditHelper;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/difficulty-levels")
public class DifficultyLevelController {

    private final DifficultyLevelService service;
    private final AuditHelper auditHelper;

    public DifficultyLevelController(DifficultyLevelService service, AuditHelper auditHelper) {
        this.service = service;
        this.auditHelper = auditHelper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public DifficultyLevelResponse create(@Valid @RequestBody CreateDifficultyLevelRequest request) {
        DifficultyLevelResponse response = service.create(request);
        auditHelper.log(AuditAction.CREATE, "DifficultyLevel", response.getId());
        return response;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<DifficultyLevelResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public DifficultyLevelResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/by-slug/{slug}")
    @PreAuthorize("isAuthenticated()")
    public DifficultyLevelResponse getBySlug(@PathVariable String slug) {
        return service.getBySlug(slug);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DifficultyLevelResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDifficultyLevelRequest request) {
        DifficultyLevelResponse response = service.update(id, request);
        auditHelper.log(AuditAction.UPDATE, "DifficultyLevel", id);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
        auditHelper.log(AuditAction.DELETE, "DifficultyLevel", id);
    }
}
