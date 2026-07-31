package com.plagiarism.engine.similarity;

public record ComparisonScore(
        double combinedScore,
        double winnowingScore,
        double cosineScore,
        double lcsScore
) {}
