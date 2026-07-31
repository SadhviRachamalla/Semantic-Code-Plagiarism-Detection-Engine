package com.plagiarism.engine.mapper;

import com.plagiarism.engine.dto.SubmissionSetResponse;
import com.plagiarism.engine.entity.SubmissionSet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionSetMapper {
    @Mapping(target = "submissionCount", expression = "java(submissionSet.getSubmissions() != null ? submissionSet.getSubmissions().size() : 0)")
    SubmissionSetResponse toResponse(SubmissionSet submissionSet);
}
