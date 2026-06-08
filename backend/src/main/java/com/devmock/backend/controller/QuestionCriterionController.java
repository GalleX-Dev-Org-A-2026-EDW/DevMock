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

import com.devmock.backend.dto.CreateQuestionCriterionRequest;
import com.devmock.backend.dto.QuestionCriterionResponse;
import com.devmock.backend.dto.UpdateQuestionCriterionRequest;
import com.devmock.backend.entity.en_enum.AuditAction;
import com.devmock.backend.service.QuestionCriterionService;
import com.devmock.backend.util.AuditHelper;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/question-criteria")
public class QuestionCriterionController {

    private final QuestionCriterionService service;
    private final AuditHelper auditHelper;

    public QuestionCriterionController(QuestionCriterionService service, AuditHelper auditHelper) {
        this.service = service;
        this.auditHelper = auditHelper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public QuestionCriterionResponse create(@Valid @RequestBody CreateQuestionCriterionRequest request) {
        QuestionCriterionResponse response = service.create(request);
        auditHelper.log(AuditAction.CREATE, "QuestionCriterion", response.getId());
        return response;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<QuestionCriterionResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public QuestionCriterionResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public QuestionCriterionResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateQuestionCriterionRequest request) {
        QuestionCriterionResponse response = service.update(id, request);
        auditHelper.log(AuditAction.UPDATE, "QuestionCriterion", id);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
        auditHelper.log(AuditAction.DELETE, "QuestionCriterion", id);
    }
}
