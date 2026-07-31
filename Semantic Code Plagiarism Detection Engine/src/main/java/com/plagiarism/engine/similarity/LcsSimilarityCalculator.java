package com.plagiarism.engine.similarity;

import com.plagiarism.engine.parser.TokenType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LcsSimilarityCalculator {

    public double calculateSimilarity(List<TokenType> tokens1, List<TokenType> tokens2) {
        if (tokens1 == null || tokens2 == null || tokens1.isEmpty() || tokens2.isEmpty()) {
            return 0.0;
        }

        int lcsLength = computeLcsLength(tokens1, tokens2);
        return (2.0 * lcsLength) / (tokens1.size() + tokens2.size());
    }

    private int computeLcsLength(List<TokenType> s1, List<TokenType> s2) {
        int m = s1.size();
        int n = s2.size();

        if (m > n) {
            List<TokenType> temp = s1;
            s1 = s2;
            s2 = temp;
            m = s1.size();
            n = s2.size();
        }

        int[] dp = new int[n + 1];

        for (int i = 1; i <= m; i++) {
            int prev = 0;
            for (int j = 1; j <= n; j++) {
                int temp = dp[j];
                if (s1.get(i - 1) == s2.get(j - 1)) {
                    dp[j] = prev + 1;
                } else {
                    dp[j] = Math.max(dp[j], dp[j - 1]);
                }
                prev = temp;
            }
        }

        return dp[n];
    }
}
