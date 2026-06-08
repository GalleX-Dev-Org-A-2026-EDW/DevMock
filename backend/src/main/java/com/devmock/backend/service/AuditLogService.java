package com.devmock.backend.service;

import java.util.List;
import java.util.UUID;

import com.devmock.backend.dto.AuditLogResponse;
import com.devmock.backend.dto.CreateAuditLogRequest;
import com.devmock.backend.dto.UpdateAuditLogRequest;
import com.devmock.backend.entity.en_enum.AuditAction;

public interface AuditLogService {

    AuditLogResponse create(CreateAuditLogRequest request);

    List<AuditLogResponse> list();

    AuditLogResponse getById(UUID id);

    AuditLogResponse update(UUID id, UpdateAuditLogRequest request);

    void delete(UUID id);

    default AuditLogResponse log(UUID userId, AuditAction action, String entityName,
            UUID entityId, String ipAddress, String userAgent) {
        CreateAuditLogRequest req = new CreateAuditLogRequest();
        req.setUserId(userId);
        req.setAction(action);
        req.setEntityName(entityName);
        req.setEntityId(entityId);
        req.setIpAddress(ipAddress);
        req.setUserAgent(userAgent);
        return create(req);
    }
}