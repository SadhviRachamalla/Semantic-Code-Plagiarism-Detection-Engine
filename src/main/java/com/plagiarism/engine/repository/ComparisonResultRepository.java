package com.plagiarism.engine.repository;

import com.plagiarism.engine.entity.ComparisonResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ComparisonResultRepository extends JpaRepository<ComparisonResult, UUID> {

    @Query("SELECT cr FROM ComparisonResult cr WHERE cr.similarityScore >= :threshold ORDER BY cr.similarityScore DESC")
    List<ComparisonResult> findFlaggedPairs(@Param("threshold") double threshold);

    @Query("SELECT cr FROM ComparisonResult cr WHERE cr.submissionA.submissionSet.id = :setId AND cr.similarityScore >= :threshold ORDER BY cr.similarityScore DESC")
    List<ComparisonResult> findFlaggedPairsBySet(@Param("setId") UUID setId, @Param("threshold") double threshold);

    @Query("SELECT cr FROM ComparisonResult cr WHERE " +
           "(cr.submissionA.id = :id1 AND cr.submissionB.id = :id2) OR " +
           "(cr.submissionA.id = :id2 AND cr.submissionB.id = :id1)")
    Optional<ComparisonResult> findPairComparison(@Param("id1") UUID id1, @Param("id2") UUID id2);
}
