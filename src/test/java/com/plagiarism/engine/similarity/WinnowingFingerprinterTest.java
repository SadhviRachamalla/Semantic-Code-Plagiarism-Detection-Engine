package com.plagiarism.engine.similarity;

import com.plagiarism.engine.parser.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WinnowingFingerprinterTest {

    @Test
    public void testGenerateFingerprints() {
        WinnowingFingerprinter fingerprinter = new WinnowingFingerprinter();
        fingerprinter.setK(3);
        fingerprinter.setW(2);

        List<TokenType> tokens = List.of(
                TokenType.FUNC_DECL,
                TokenType.VAR_DECL,
                TokenType.ASSIGN,
                TokenType.IF,
                TokenType.RETURN
        );

        List<WinnowingFingerprinter.Fingerprint> fingerprints = fingerprinter.generateFingerprints(tokens);

        assertNotNull(fingerprints);
        assertFalse(fingerprints.isEmpty());

        double sim = fingerprinter.calculateSimilarity(fingerprints, fingerprints);
        assertEquals(1.0, sim, 0.0001);
    }

    @Test
    public void testEmptySimilarity() {
        WinnowingFingerprinter fingerprinter = new WinnowingFingerprinter();
        double sim = fingerprinter.calculateSimilarity(List.of(), List.of());
        assertEquals(0.0, sim);
    }
}
