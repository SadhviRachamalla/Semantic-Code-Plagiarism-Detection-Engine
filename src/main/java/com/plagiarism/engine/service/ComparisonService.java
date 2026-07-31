package com.plagiarism.engine.service;

import com.plagiarism.engine.dto.ComparisonResultResponse;
import com.plagiarism.engine.dto.FlaggedPairResponse;
import com.plagiarism.engine.entity.ComparisonResult;
import com.plagiarism.engine.entity.DbFingerprint;
import com.plagiarism.engine.entity.Submission;
import com.plagiarism.engine.mapper.ComparisonMapper;
import com.plagiarism.engine.repository.ComparisonResultRepository;
import com.plagiarism.engine.repository.SubmissionRepository;
import com.plagiarism.engine.similarity.ComparisonScore;
import com.plagiarism.engine.similarity.SimilarityEngine;
import com.plagiarism.engine.similarity.WinnowingFingerprinter.Fingerprint;
import com.plagiarism.engine.parser.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComparisonService {
    private static final Logger log = LoggerFactory.getLogger(ComparisonService.class);

    private final SubmissionRepository submissionRepository;
    private final ComparisonResultRepository comparisonResultRepository;
    private final SimilarityEngine similarityEngine;
    private final ComparisonMapper comparisonMapper;
    private final AuditService auditService;

    public ComparisonService(SubmissionRepository submissionRepository,
                             ComparisonResultRepository comparisonResultRepository,
                             SimilarityEngine similarityEngine,
                             ComparisonMapper comparisonMapper,
                             AuditService auditService) {
        this.submissionRepository = submissionRepository;
        this.comparisonResultRepository = comparisonResultRepository;
        this.similarityEngine = similarityEngine;
        this.comparisonMapper = comparisonMapper;
        this.auditService = auditService;
    }

    public ComparisonResultResponse comparePair(UUID id1, UUID id2) {
        if (id1.equals(id2)) {
            throw new IllegalArgumentException("Cannot compare a submission with itself");
        }

        // Check if comparison already exists
        Optional<ComparisonResult> existing = comparisonResultRepository.findPairComparison(id1, id2);
        if (existing.isPresent()) {
            return comparisonMapper.toResponse(existing.get());
        }

        Submission sA = submissionRepository.findById(id1)
                .orElseThrow(() -> new com.plagiarism.engine.exception.EntityNotFoundException("Submission A not found"));
        Submission sB = submissionRepository.findById(id2)
                .orElseThrow(() -> new com.plagiarism.engine.exception.EntityNotFoundException("Submission B not found"));

        ComparisonResult result = performComparison(sA, sB);
        ComparisonResult saved = comparisonResultRepository.save(result);

        auditService.logAction("COMPARE_PAIR", 
                String.format("Compared '%s' and '%s'. Combined score: %.4f", sA.getName(), sB.getName(), saved.getSimilarityScore()));

        return comparisonMapper.toResponse(saved);
    }

    @Async("comparisonTaskExecutor")
    public void compareSetAsync(UUID setId, double threshold) {
        auditService.logAction("START_BATCH_COMPARE", "Starting asynchronous batch comparison for set: " + setId);

        List<Submission> submissions = submissionRepository.findBySubmissionSetId(setId);
        if (submissions.size() < 2) {
            auditService.logAction("BATCH_COMPARE_SKIP", "Skipping batch comparison. Set size is " + submissions.size());
            return;
        }

        int comparisonsRun = 0;
        int flaggedCount = 0;

        for (int i = 0; i < submissions.size(); i++) {
            for (int j = i + 1; j < submissions.size(); j++) {
                Submission sA = submissions.get(i);
                Submission sB = submissions.get(j);

                try {
                    // Check if already compared
                    Optional<ComparisonResult> existing = comparisonResultRepository.findPairComparison(sA.getId(), sB.getId());
                    ComparisonResult result;
                    if (existing.isPresent()) {
                        result = existing.get();
                    } else {
                        result = performComparison(sA, sB);
                        comparisonResultRepository.save(result);
                        comparisonsRun++;
                    }

                    if (result.getSimilarityScore() >= threshold) {
                        flaggedCount++;
                    }
                } catch (Exception e) {
                    log.error("Failed to compare pair {} and {}: {}", sA.getId(), sB.getId(), e.getMessage());
                }
            }
        }

        auditService.logAction("END_BATCH_COMPARE", 
                String.format("Completed batch comparison for set %s. Ran %d new comparisons. Found %d flagged pairs above %.2f threshold", 
                        setId, comparisonsRun, flaggedCount, threshold));
    }

    private ComparisonResult performComparison(Submission sA, Submission sB) {
        ComparisonResult result = new ComparisonResult();
        result.setSubmissionA(sA);
        result.setSubmissionB(sB);

        // Mismatched language returns 0 similarity
        if (!sA.getLanguage().equalsIgnoreCase(sB.getLanguage())) {
            result.setSimilarityScore(0.0);
            result.setWinnowingScore(0.0);
            result.setCosineScore(0.0);
            result.setLcsScore(0.0);
            return result;
        }

        List<TokenType> tA = deserializeTokens(sA.getNormalizedTokens());
        List<TokenType> tB = deserializeTokens(sB.getNormalizedTokens());

        List<Fingerprint> fA = deserializeFingerprints(sA.getFingerprints());
        List<Fingerprint> fB = deserializeFingerprints(sB.getFingerprints());

        ComparisonScore score = similarityEngine.compare(tA, fA, tB, fB);

        result.setSimilarityScore(score.combinedScore());
        result.setWinnowingScore(score.winnowingScore());
        result.setCosineScore(score.cosineScore());
        result.setLcsScore(score.lcsScore());

        return result;
    }

    @Transactional(readOnly = true)
    public List<FlaggedPairResponse> getFlaggedPairs(double threshold) {
        return comparisonResultRepository.findFlaggedPairs(threshold).stream()
                .map(comparisonMapper::toFlaggedResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FlaggedPairResponse> getFlaggedPairsBySet(UUID setId, double threshold) {
        return comparisonResultRepository.findFlaggedPairsBySet(setId, threshold).stream()
                .map(comparisonMapper::toFlaggedResponse)
                .collect(Collectors.toList());
    }

    private List<TokenType> deserializeTokens(String tokenStr) {
        List<TokenType> tokens = new ArrayList<>();
        for (char c : tokenStr.toCharArray()) {
            String sym = String.valueOf(c);
            for (TokenType type : TokenType.values()) {
                if (type.getSymbol().equals(sym)) {
                    tokens.add(type);
                    break;
                }
            }
        }
        return tokens;
    }

    private List<Fingerprint> deserializeFingerprints(List<DbFingerprint> dbFingerprints) {
        return dbFingerprints.stream()
                .map(df -> new Fingerprint(df.getHashValue(), df.getPosition()))
                .collect(Collectors.toList());
    }
}
