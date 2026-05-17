package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.AuditLogResponse;
import com.devmock.backend.dto.CreateAuditLogRequest;
import com.devmock.backend.dto.UpdateAuditLogRequest;

public interface AuditLogService {

    AuditLogResponse create(CreateAuditLogRequest request);

    List<AuditLogResponse> list();

    AuditLogResponse getById(UUID id);

    AuditLogResponse update(UUID id, UpdateAuditLogRequest request);

    void delete(UUID id);
}