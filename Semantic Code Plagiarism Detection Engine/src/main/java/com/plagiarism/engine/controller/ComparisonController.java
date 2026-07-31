package com.plagiarism.engine.controller;

import com.plagiarism.engine.dto.ComparisonResultResponse;
import com.plagiarism.engine.dto.FlaggedPairResponse;
import com.plagiarism.engine.service.ComparisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comparisons")
@Tag(name = "Comparisons", description = "Endpoints for triggering plagiarism checks and fetching similarity reports")
@SecurityRequirement(name = "ApiKeyAuth")
public class ComparisonController {

    private final ComparisonService comparisonService;

    public ComparisonController(ComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @PostMapping("/pair")
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "Perform pairwise comparison of two submissions")
    public ResponseEntity<ComparisonResultResponse> comparePair(
            @RequestParam UUID submissionAId,
            @RequestParam UUID submissionBId) {
        return ResponseEntity.ok(comparisonService.comparePair(submissionAId, submissionBId));
    }

    @PostMapping("/sets/{setId}")
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "Trigger asynchronous batch comparison across a submission set")
    public ResponseEntity<String> compareSet(
            @PathVariable UUID setId,
            @RequestParam(defaultValue = "0.50") double threshold) {
        comparisonService.compareSetAsync(setId, threshold);
        return ResponseEntity.accepted().body("Asynchronous batch comparison initiated for submission set " + setId);
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "Fetch list of all flagged pairs across all sets exceeding the similarity threshold")
    public ResponseEntity<List<FlaggedPairResponse>> getFlaggedPairs(
            @RequestParam(defaultValue = "0.50") double threshold) {
        return ResponseEntity.ok(comparisonService.getFlaggedPairs(threshold));
    }

    @GetMapping("/reports/sets/{setId}")
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "Fetch similarity reports for a specific submission set")
    public ResponseEntity<List<FlaggedPairResponse>> getFlaggedPairsBySet(
            @PathVariable UUID setId,
            @RequestParam(defaultValue = "0.50") double threshold) {
        return ResponseEntity.ok(comparisonService.getFlaggedPairsBySet(setId, threshold));
    }
}
