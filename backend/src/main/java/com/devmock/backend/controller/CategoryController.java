package com.devmock.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.devmock.backend.dto.CategoryResponse;
import com.devmock.backend.dto.CreateCategoryRequest;
import com.devmock.backend.dto.UpdateCategoryRequest;
import com.devmock.backend.entity.en_enum.AuditAction;
import com.devmock.backend.service.CategoryService;
import com.devmock.backend.util.AuditHelper;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;
    private final AuditHelper auditHelper;

    public CategoryController(CategoryService service, AuditHelper auditHelper) {
        this.service = service;
        this.auditHelper = auditHelper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse create(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = service.create(request);
        auditHelper.log(AuditAction.CREATE, "Category", response.getId());
        return response;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CategoryResponse> list(@RequestParam(name = "activeOnly", required = false, defaultValue = "false") boolean activeOnly) {
        return activeOnly ? service.listActive() : service.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CategoryResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/by-slug/{slug}")
    @PreAuthorize("isAuthenticated()")
    public CategoryResponse getBySlug(@PathVariable String slug) {
        return service.getBySlug(slug);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryResponse response = service.update(id, request);
        auditHelper.log(AuditAction.UPDATE, "Category", id);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
        auditHelper.log(AuditAction.DELETE, "Category", id);
    }
}
