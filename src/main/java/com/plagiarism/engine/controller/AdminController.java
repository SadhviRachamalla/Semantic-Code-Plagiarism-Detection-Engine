package com.plagiarism.engine.controller;

import com.plagiarism.engine.dto.AuditLogResponse;
import com.plagiarism.engine.service.AuditService;
import com.plagiarism.engine.similarity.SimilarityEngine;
import com.plagiarism.engine.similarity.WinnowingFingerprinter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Administrative endpoints for viewing audit logs and configuring parameters")
@SecurityRequirement(name = "ApiKeyAuth")
public class AdminController {

    private final AuditService auditService;
    private final SimilarityEngine similarityEngine;
    private final WinnowingFingerprinter fingerprinter;

    public AdminController(AuditService auditService,
                           SimilarityEngine similarityEngine,
                           WinnowingFingerprinter fingerprinter) {
        this.auditService = auditService;
        this.similarityEngine = similarityEngine;
        this.fingerprinter = fingerprinter;
    }

    @GetMapping("/audits")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "View audit logs (admin only)")
    public ResponseEntity<List<AuditLogResponse>> getAudits() {
        return ResponseEntity.ok(auditService.getAllLogs());
    }

    @PostMapping("/config/weights")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Configure weights for similarity scoring algorithms (admin only)")
    public ResponseEntity<String> updateWeights(
            @RequestParam double winnowing,
            @RequestParam double cosine,
            @RequestParam double lcs) {
        similarityEngine.setWeights(winnowing, cosine, lcs);
        auditService.logAction("CONFIG_UPDATE", 
                String.format("Updated weights: Winnowing=%.2f, Cosine=%.2f, LCS=%.2f", winnowing, cosine, lcs));
        return ResponseEntity.ok("Weights updated successfully");
    }

    @PostMapping("/config/winnowing")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Configure Winnowing sliding window sizes (admin only)")
    public ResponseEntity<String> updateWinnowingConfig(
            @RequestParam int k,
            @RequestParam int w) {
        if (k <= 0 || w <= 0) {
            throw new IllegalArgumentException("k and w values must be greater than zero");
        }
        fingerprinter.setK(k);
        fingerprinter.setW(w);
        auditService.logAction("CONFIG_UPDATE", 
                String.format("Updated winnowing sliding window: k-gram size=%d, window size=%d", k, w));
        return ResponseEntity.ok("Winnowing configurations updated successfully");
    }
}
