package com.plagiarism.engine.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ComparisonResultResponse {
    private UUID id;
    private UUID submissionAId;
    private UUID submissionBId;
    private double similarityScore;
    private double winnowingScore;
    private double cosineScore;
    private double lcsScore;
    private LocalDateTime createdAt;
}
