package com.plagiarism.engine.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SubmissionRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Language is required (java, python, cpp)")
    private String language;

    @NotBlank(message = "Source code is required")
    private String sourceCode;

    private UUID submissionSetId;
}
