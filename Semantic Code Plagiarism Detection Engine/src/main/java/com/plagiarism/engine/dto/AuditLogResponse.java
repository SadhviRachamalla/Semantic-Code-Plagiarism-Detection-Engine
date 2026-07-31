package com.plagiarism.engine.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AuditLogResponse {
    private UUID id;
    private String action;
    private String details;
    private String performedBy;
    private LocalDateTime createdAt;
}
