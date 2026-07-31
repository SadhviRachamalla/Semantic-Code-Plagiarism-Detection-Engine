package com.plagiarism.engine.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FlaggedPairResponse {
    private UUID comparisonId;
    private SubmissionResponse submissionA;
    private SubmissionResponse submissionB;
    private double similarityScore;
    private double winnowingScore;
    private double cosineScore;
    private double lcsScore;
}
