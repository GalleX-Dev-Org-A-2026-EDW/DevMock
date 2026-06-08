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

import com.devmock.backend.dto.CreateQuestionRequest;
import com.devmock.backend.dto.QuestionResponse;
import com.devmock.backend.dto.UpdateQuestionRequest;
import com.devmock.backend.entity.en_enum.AuditAction;
import com.devmock.backend.service.QuestionService;
import com.devmock.backend.util.AuditHelper;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService service;
    private final AuditHelper auditHelper;

    public QuestionController(QuestionService service, AuditHelper auditHelper) {
        this.service = service;
        this.auditHelper = auditHelper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public QuestionResponse create(@Valid @RequestBody CreateQuestionRequest request) {
        QuestionResponse response = service.create(request);
        auditHelper.log(AuditAction.CREATE, "Question", response.getId());
        return response;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<QuestionResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public QuestionResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public QuestionResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateQuestionRequest request) {
        QuestionResponse response = service.update(id, request);
        auditHelper.log(AuditAction.UPDATE, "Question", id);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
        auditHelper.log(AuditAction.DELETE, "Question", id);
    }
}
