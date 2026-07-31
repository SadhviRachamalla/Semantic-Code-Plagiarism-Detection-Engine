package com.plagiarism.engine.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SubmissionResponse {
    private UUID id;
    private String name;
    private String language;
    private String fileHash;
    private LocalDateTime createdAt;
    private UUID submissionSetId;
}
