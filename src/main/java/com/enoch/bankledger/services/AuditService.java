package com.enoch.bankledger.services;

import com.enoch.bankledger.entity.AuditLog;
import com.enoch.bankledger.repository.AuditLogRepository;
import com.enoch.bankledger.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserService currentUserService;

    public void log(String action, String entityType, Long entityId, String details) {
        String performedBy = "SYSTEM";

        try {
            performedBy = currentUserService.getCurrentUsername();
        } catch (Exception ignored) {
            // In case there's no authenticated user (e.g. during registration)
        }

        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .performedBy(performedBy)
                .details(details)
                .build();

        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<AuditLog> getMyLogs() {
        String username = currentUserService.getCurrentUsername();
        return auditLogRepository.findByPerformedByOrderByCreatedAtDesc(username);
    }

    public List<AuditLog> getLogsForEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }
}