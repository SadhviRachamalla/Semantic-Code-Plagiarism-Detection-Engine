package com.plagiarism.engine.service;

import com.plagiarism.engine.dto.AuditLogResponse;
import com.plagiarism.engine.entity.AuditLog;
import com.plagiarism.engine.mapper.AuditLogMapper;
import com.plagiarism.engine.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    public AuditService(AuditLogRepository auditLogRepository, AuditLogMapper auditLogMapper) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogMapper = auditLogMapper;
    }

    public void logAction(String action, String details) {
        String user = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "SYSTEM";
        
        AuditLog audit = new AuditLog();
        audit.setAction(action);
        audit.setDetails(details);
        audit.setPerformedBy(user);
        auditLogRepository.save(audit);

        log.info("AUDIT LOG - Action: {}, User: {}, Details: {}", action, user, details);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(auditLogMapper::toResponse)
                .collect(Collectors.toList());
    }
}
