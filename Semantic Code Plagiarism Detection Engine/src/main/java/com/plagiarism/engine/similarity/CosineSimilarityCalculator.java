package com.plagiarism.engine.similarity;

import com.plagiarism.engine.parser.TokenType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class CosineSimilarityCalculator {

    public double calculateSimilarity(List<TokenType> tokens1, List<TokenType> tokens2) {
        if (tokens1 == null || tokens2 == null || tokens1.isEmpty() || tokens2.isEmpty()) {
            return 0.0;
        }

        Map<TokenType, Integer> freq1 = getFrequencyMap(tokens1);
        Map<TokenType, Integer> freq2 = getFrequencyMap(tokens2);

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (TokenType type : TokenType.values()) {
            int count1 = freq1.getOrDefault(type, 0);
            int count2 = freq2.getOrDefault(type, 0);

            dotProduct += count1 * count2;
            norm1 += count1 * count1;
            norm2 += count2 * count2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private Map<TokenType, Integer> getFrequencyMap(List<TokenType> tokens) {
        Map<TokenType, Integer> frequencies = new EnumMap<>(TokenType.class);
        for (TokenType token : tokens) {
            frequencies.put(token, frequencies.getOrDefault(token, 0) + 1);
        }
        return frequencies;
    }
}
