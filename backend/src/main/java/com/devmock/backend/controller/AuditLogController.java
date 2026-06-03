package com.devmock.backend.controller;

import com.devmock.backend.dto.AuditLogResponse;
import com.devmock.backend.dto.CreateAuditLogRequest;
import com.devmock.backend.dto.UpdateAuditLogRequest;
import com.devmock.backend.service.AuditLogService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuditLogResponse create(
            @Valid @RequestBody CreateAuditLogRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<AuditLogResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public AuditLogResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public AuditLogResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAuditLogRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}