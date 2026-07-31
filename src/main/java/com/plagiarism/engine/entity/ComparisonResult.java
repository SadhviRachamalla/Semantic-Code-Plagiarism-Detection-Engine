package com.plagiarism.engine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comparison_results")
@Getter
@Setter
public class ComparisonResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_a_id", nullable = false)
    private Submission submissionA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_b_id", nullable = false)
    private Submission submissionB;

    @Column(name = "similarity_score", nullable = false)
    private double similarityScore;

    @Column(name = "winnowing_score", nullable = false)
    private double winnowingScore;

    @Column(name = "cosine_score", nullable = false)
    private double cosineScore;

    @Column(name = "lcs_score", nullable = false)
    private double lcsScore;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
