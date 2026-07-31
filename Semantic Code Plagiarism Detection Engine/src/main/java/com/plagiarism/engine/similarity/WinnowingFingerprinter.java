package com.plagiarism.engine.similarity;

import com.plagiarism.engine.parser.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
public class WinnowingFingerprinter {

    public record Fingerprint(long hash, int position) implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    @Value("${plagiarism.winnowing.k:5}")
    private int k = 5;

    @Value("${plagiarism.winnowing.w:4}")
    private int w = 4;

    public List<Fingerprint> generateFingerprints(List<TokenType> tokens) {
        if (tokens == null || tokens.size() < k) {
            return Collections.emptyList();
        }

        StringBuilder sb = new StringBuilder();
        for (TokenType token : tokens) {
            sb.append(token.getSymbol());
        }
        String tokenStr = sb.toString();

        int numKgrams = tokenStr.length() - k + 1;
        long[] hashes = new long[numKgrams];
        for (int i = 0; i < numKgrams; i++) {
            hashes[i] = hashKgram(tokenStr.substring(i, i + k));
        }

        List<Fingerprint> fingerprints = new ArrayList<>();
        int lastSelectedPos = -1;

        for (int i = 0; i <= numKgrams - w; i++) {
            int minIdx = i;
            long minHash = hashes[i];
            for (int j = i + 1; j < i + w; j++) {
                if (hashes[j] < minHash) {
                    minHash = hashes[j];
                    minIdx = j;
                } else if (hashes[j] == minHash) {
                    minIdx = j; // rightmost minimum tie-breaker
                }
            }

            if (minIdx != lastSelectedPos) {
                fingerprints.add(new Fingerprint(minHash, minIdx));
                lastSelectedPos = minIdx;
            }
        }

        return fingerprints;
    }

    public double calculateSimilarity(List<Fingerprint> f1, List<Fingerprint> f2) {
        if (f1 == null || f2 == null || f1.isEmpty() || f2.isEmpty()) {
            return 0.0;
        }

        Set<Long> set1 = new HashSet<>();
        for (Fingerprint f : f1) {
            set1.add(f.hash());
        }

        Set<Long> set2 = new HashSet<>();
        for (Fingerprint f : f2) {
            set2.add(f.hash());
        }

        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<Long> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    private long hashKgram(String kgram) {
        long hash = 0;
        long prime = 31;
        for (int i = 0; i < kgram.length(); i++) {
            hash = hash * prime + kgram.charAt(i);
        }
        return hash;
    }

    public void setK(int k) {
        this.k = k;
    }

    public void setW(int w) {
        this.w = w;
    }
}
