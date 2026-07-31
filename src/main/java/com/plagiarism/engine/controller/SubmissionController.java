package com.plagiarism.engine.controller;

import com.plagiarism.engine.dto.*;
import com.plagiarism.engine.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
@Tag(name = "Submissions", description = "Endpoints for managing source code submissions and submission sets")
@SecurityRequirement(name = "ApiKeyAuth")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/sets")
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "Create a new submission set (e.g. classroom assignment)")
    public ResponseEntity<SubmissionSetResponse> createSubmissionSet(@Valid @RequestBody SubmissionSetRequest request) {
        return new ResponseEntity<>(submissionService.createSubmissionSet(request), HttpStatus.CREATED);
    }

    @GetMapping("/sets")
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "List all submission sets")
    public ResponseEntity<List<SubmissionSetResponse>> getAllSubmissionSets() {
        return ResponseEntity.ok(submissionService.getAllSubmissionSets());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "Upload a single source code submission")
    public ResponseEntity<SubmissionResponse> uploadSubmission(@Valid @RequestBody SubmissionRequest request) {
        return new ResponseEntity<>(submissionService.uploadSubmission(request), HttpStatus.CREATED);
    }

    @PostMapping(value = "/sets/{setId}/zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "Upload and unpack a ZIP archive of submissions into a set")
    public ResponseEntity<List<SubmissionResponse>> uploadZip(
            @PathVariable UUID setId,
            @Parameter(description = "Zip file containing java, python, or cpp code files")
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(submissionService.uploadZip(setId, file));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "Fetch submission metadata by ID")
    public ResponseEntity<SubmissionResponse> getSubmission(@PathVariable UUID id) {
        return ResponseEntity.ok(submissionService.getSubmission(id));
    }

    @GetMapping("/sets/{setId}")
    @PreAuthorize("hasRole('REVIEWER')")
    @Operation(summary = "List all submissions within a set")
    public ResponseEntity<List<SubmissionResponse>> getSubmissionsInSet(@PathVariable UUID setId) {
        return ResponseEntity.ok(submissionService.getSubmissionsInSet(setId));
    }
}
