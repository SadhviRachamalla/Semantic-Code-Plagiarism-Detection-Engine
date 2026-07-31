package com.plagiarism.engine.repository;

import com.plagiarism.engine.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findBySubmissionSetId(UUID submissionSetId);

    Optional<Submission> findFirstByFileHash(String fileHash);

    @Query("SELECT s FROM Submission s WHERE s.submissionSet.id = :setId AND s.id <> :excludeId")
    List<Submission> findBySubmissionSetIdAndIdNot(@Param("setId") UUID setId, @Param("excludeId") UUID excludeId);
}
