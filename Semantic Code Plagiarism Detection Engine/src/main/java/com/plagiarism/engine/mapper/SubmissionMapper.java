package com.plagiarism.engine.mapper;

import com.plagiarism.engine.dto.SubmissionResponse;
import com.plagiarism.engine.entity.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {
    @Mapping(target = "submissionSetId", source = "submissionSet.id")
    SubmissionResponse toResponse(Submission submission);
}
