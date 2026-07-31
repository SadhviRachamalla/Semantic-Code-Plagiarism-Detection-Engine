package com.plagiarism.engine.mapper;

import com.plagiarism.engine.dto.AuditLogResponse;
import com.plagiarism.engine.entity.AuditLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    AuditLogResponse toResponse(AuditLog auditLog);
}
