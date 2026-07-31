package com.plagiarism.engine.similarity;

import com.plagiarism.engine.parser.TokenType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimilarityEngineTest {

    @Test
    public void testSimilarityCalculations() {
        WinnowingFingerprinter fingerprinter = new WinnowingFingerprinter();
        fingerprinter.setK(3);
        fingerprinter.setW(2);
        
        CosineSimilarityCalculator cosine = new CosineSimilarityCalculator();
        LcsSimilarityCalculator lcs = new LcsSimilarityCalculator();

        SimilarityEngine engine = new SimilarityEngine(fingerprinter, cosine, lcs);
        engine.setWeights(0.60, 0.20, 0.20);

        List<TokenType> t1 = List.of(TokenType.FUNC_DECL, TokenType.VAR_DECL, TokenType.ASSIGN, TokenType.IF, TokenType.RETURN);
        List<TokenType> t2 = List.of(TokenType.FUNC_DECL, TokenType.VAR_DECL, TokenType.ASSIGN, TokenType.IF, TokenType.RETURN);

        List<WinnowingFingerprinter.Fingerprint> f1 = fingerprinter.generateFingerprints(t1);
        List<WinnowingFingerprinter.Fingerprint> f2 = fingerprinter.generateFingerprints(t2);

        ComparisonScore score = engine.compare(t1, f1, t2, f2);

        assertEquals(1.0, score.combinedScore(), 0.0001);
        assertEquals(1.0, score.winnowingScore(), 0.0001);
        assertEquals(1.0, score.cosineScore(), 0.0001);
        assertEquals(1.0, score.lcsScore(), 0.0001);
    }

    @Test
    public void testDivergentCode() {
        WinnowingFingerprinter fingerprinter = new WinnowingFingerprinter();
        fingerprinter.setK(3);
        fingerprinter.setW(2);
        
        CosineSimilarityCalculator cosine = new CosineSimilarityCalculator();
        LcsSimilarityCalculator lcs = new LcsSimilarityCalculator();

        SimilarityEngine engine = new SimilarityEngine(fingerprinter, cosine, lcs);
        engine.setWeights(0.60, 0.20, 0.20);

        List<TokenType> t1 = List.of(TokenType.FUNC_DECL, TokenType.VAR_DECL, TokenType.ASSIGN);
        List<TokenType> t2 = List.of(TokenType.IF, TokenType.FOR, TokenType.WHILE);

        List<WinnowingFingerprinter.Fingerprint> f1 = fingerprinter.generateFingerprints(t1);
        List<WinnowingFingerprinter.Fingerprint> f2 = fingerprinter.generateFingerprints(t2);

        ComparisonScore score = engine.compare(t1, f1, t2, f2);

        // Should be 0 or extremely low due to completely disjoint token pools
        assertEquals(0.0, score.combinedScore(), 0.0001);
    }
}
