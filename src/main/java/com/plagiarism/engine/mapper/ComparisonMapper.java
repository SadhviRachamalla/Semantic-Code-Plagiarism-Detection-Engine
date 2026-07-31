package com.plagiarism.engine.mapper;

import com.plagiarism.engine.dto.ComparisonResultResponse;
import com.plagiarism.engine.dto.FlaggedPairResponse;
import com.plagiarism.engine.entity.ComparisonResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SubmissionMapper.class})
public interface ComparisonMapper {
    @Mapping(target = "submissionAId", source = "submissionA.id")
    @Mapping(target = "submissionBId", source = "submissionB.id")
    ComparisonResultResponse toResponse(ComparisonResult result);

    @Mapping(target = "comparisonId", source = "id")
    @Mapping(target = "submissionA", source = "submissionA")
    @Mapping(target = "submissionB", source = "submissionB")
    FlaggedPairResponse toFlaggedResponse(ComparisonResult result);
}
