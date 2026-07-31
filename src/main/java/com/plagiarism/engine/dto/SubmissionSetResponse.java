package com.plagiarism.engine.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SubmissionSetResponse {
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
    private int submissionCount;
}
