package com.plagiarism.engine.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionSetRequest {
    @NotBlank(message = "Submission set name is required")
    private String name;
}
