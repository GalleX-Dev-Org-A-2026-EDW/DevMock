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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devmock.backend.dto.AnswerOptionResponse;
import com.devmock.backend.dto.CreateAnswerOptionRequest;
import com.devmock.backend.dto.UpdateAnswerOptionRequest;
import com.devmock.backend.entity.en_enum.AuditAction;
import com.devmock.backend.service.AnswerOptionService;
import com.devmock.backend.util.AuditHelper;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/answer-options")
public class AnswerOptionController {

    private final AnswerOptionService service;
    private final AuditHelper auditHelper;

    public AnswerOptionController(AnswerOptionService service, AuditHelper auditHelper) {
        this.service = service;
        this.auditHelper = auditHelper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public AnswerOptionResponse create(@Valid @RequestBody CreateAnswerOptionRequest request) {
        AnswerOptionResponse response = service.create(request);
        auditHelper.log(AuditAction.CREATE, "AnswerOption", response.getId());
        return response;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<AnswerOptionResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public AnswerOptionResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AnswerOptionResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAnswerOptionRequest request) {
        AnswerOptionResponse response = service.update(id, request);
        auditHelper.log(AuditAction.UPDATE, "AnswerOption", id);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
        auditHelper.log(AuditAction.DELETE, "AnswerOption", id);
    }
}
