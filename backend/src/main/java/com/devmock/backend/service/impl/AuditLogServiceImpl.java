package com.devmock.backend.service.impl;

import com.devmock.backend.dto.AuditLogResponse;
import com.devmock.backend.dto.CreateAuditLogRequest;
import com.devmock.backend.dto.UpdateAuditLogRequest;
import com.devmock.backend.entity.AuditLog;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.AuditLogRepository;
import com.devmock.backend.service.AuditLogService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogServiceImpl(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditLogResponse create(CreateAuditLogRequest request) {

        AuditLog log = new AuditLog();

        log.setAction(request.getAction());
        log.setEntityName(request.getEntityName());
        log.setEntityId(request.getEntityId());
        log.setOldValues(request.getOldValues());
        log.setNewValues(request.getNewValues());
        log.setIpAddress(request.getIpAddress());
        log.setUserAgent(request.getUserAgent());

        AuditLog saved = repository.save(log);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> list() {

        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse getById(UUID id) {

        AuditLog log = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "AuditLog " + id + " not found"));

        return toResponse(log);
    }

    @Override
    public AuditLogResponse update(
            UUID id,
            UpdateAuditLogRequest request) {

        AuditLog log = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "AuditLog " + id + " not found"));

        if (request.getAction() != null)
            log.setAction(request.getAction());

        if (request.getEntityName() != null)
            log.setEntityName(request.getEntityName());

        if (request.getEntityId() != null)
            log.setEntityId(request.getEntityId());

        if (request.getOldValues() != null)
            log.setOldValues(request.getOldValues());

        if (request.getNewValues() != null)
            log.setNewValues(request.getNewValues());

        if (request.getIpAddress() != null)
            log.setIpAddress(request.getIpAddress());

        if (request.getUserAgent() != null)
            log.setUserAgent(request.getUserAgent());

        return toResponse(repository.save(log));
    }

    @Override
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "AuditLog " + id + " not found");
        }

        repository.deleteById(id);
    }

    // Mapper

    private AuditLogResponse toResponse(AuditLog log) {

        AuditLogResponse response = new AuditLogResponse();

        response.setId(log.getId());
        response.setAction(log.getAction());
        response.setEntityName(log.getEntityName());
        response.setEntityId(log.getEntityId());
        response.setOldValues(log.getOldValues());
        response.setNewValues(log.getNewValues());
        response.setIpAddress(log.getIpAddress());
        response.setUserAgent(log.getUserAgent());
        response.setCreatedAt(log.getCreatedAt());

        return response;
    }
}