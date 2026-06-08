package com.devmock.backend.util;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.devmock.backend.entity.User;
import com.devmock.backend.entity.en_enum.AuditAction;
import com.devmock.backend.repository.UserRepository;
import com.devmock.backend.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuditHelper {

    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public AuditHelper(AuditLogService auditLogService, UserRepository userRepository) {
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    public void log(AuditAction action, String entityName, UUID entityId) {
        UUID userId = resolveCurrentUserId();
        HttpServletRequest request = resolveRequest();
        auditLogService.log(userId, action, entityName, entityId,
                extractIp(request), request.getHeader("User-Agent"));
    }

    public void log(UUID userId, AuditAction action, String entityName, UUID entityId) {
        HttpServletRequest request = resolveRequest();
        auditLogService.log(userId, action, entityName, entityId,
                extractIp(request), request.getHeader("User-Agent"));
    }

    private UUID resolveCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepository.findByEmailAndDeletedAtIsNull(auth.getName())
                .map(User::getId)
                .orElse(null);
    }

    private HttpServletRequest resolveRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
    }

    private String extractIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
