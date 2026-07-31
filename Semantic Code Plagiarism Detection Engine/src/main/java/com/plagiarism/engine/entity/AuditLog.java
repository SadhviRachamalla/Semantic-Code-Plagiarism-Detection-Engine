package com.plagiarism.engine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String details;

    @Column(name = "performed_by", nullable = false)
    private String performedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
