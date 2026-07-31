package com.plagiarism.engine.similarity;

import com.plagiarism.engine.parser.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SimilarityEngine {

    private final WinnowingFingerprinter fingerprinter;
    private final CosineSimilarityCalculator cosineCalculator;
    private final LcsSimilarityCalculator lcsCalculator;

    @Value("${plagiarism.weights.winnowing:0.60}")
    private double wWinnowing = 0.60;

    @Value("${plagiarism.weights.cosine:0.20}")
    private double wCosine = 0.20;

    @Value("${plagiarism.weights.lcs:0.20}")
    private double wLcs = 0.20;

    public SimilarityEngine(WinnowingFingerprinter fingerprinter,
                            CosineSimilarityCalculator cosineCalculator,
                            LcsSimilarityCalculator lcsCalculator) {
        this.fingerprinter = fingerprinter;
        this.cosineCalculator = cosineCalculator;
        this.lcsCalculator = lcsCalculator;
    }

    public ComparisonScore compare(List<TokenType> tokens1,
                                   List<WinnowingFingerprinter.Fingerprint> fingerprints1,
                                   List<TokenType> tokens2,
                                   List<WinnowingFingerprinter.Fingerprint> fingerprints2) {
        
        double winnowingScore = fingerprinter.calculateSimilarity(fingerprints1, fingerprints2);
        double cosineScore = cosineCalculator.calculateSimilarity(tokens1, tokens2);
        double lcsScore = lcsCalculator.calculateSimilarity(tokens1, tokens2);

        double sumWeights = wWinnowing + wCosine + wLcs;
        double normWinnowing = wWinnowing / (sumWeights > 0 ? sumWeights : 1.0);
        double normCosine = wCosine / (sumWeights > 0 ? sumWeights : 1.0);
        double normLcs = wLcs / (sumWeights > 0 ? sumWeights : 1.0);

        double combinedScore = (winnowingScore * normWinnowing)
                + (cosineScore * normCosine)
                + (lcsScore * normLcs);

        combinedScore = Math.round(combinedScore * 10000.0) / 10000.0;
        winnowingScore = Math.round(winnowingScore * 10000.0) / 10000.0;
        cosineScore = Math.round(cosineScore * 10000.0) / 10000.0;
        lcsScore = Math.round(lcsScore * 10000.0) / 10000.0;

        return new ComparisonScore(combinedScore, winnowingScore, cosineScore, lcsScore);
    }

    public void setWeights(double winnowing, double cosine, double lcs) {
        this.wWinnowing = winnowing;
        this.wCosine = cosine;
        this.wLcs = lcs;
    }
}
