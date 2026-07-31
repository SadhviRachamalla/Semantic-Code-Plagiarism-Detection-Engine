package com.plagiarism.engine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "submissions")
@Getter
@Setter
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String language;

    @Column(name = "source_code", nullable = false, columnDefinition = "TEXT")
    private String sourceCode;

    @Column(name = "normalized_tokens", nullable = false, columnDefinition = "TEXT")
    private String normalizedTokens;

    @Column(name = "file_hash", nullable = false)
    private String fileHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_set_id")
    private SubmissionSet submissionSet;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "submission_fingerprints", joinColumns = @JoinColumn(name = "submission_id"))
    @OrderColumn(name = "fingerprint_order")
    private List<DbFingerprint> fingerprints = new ArrayList<>();
}
