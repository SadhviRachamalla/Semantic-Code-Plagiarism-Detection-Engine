package com.plagiarism.engine.repository;

import com.plagiarism.engine.entity.SubmissionSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubmissionSetRepository extends JpaRepository<SubmissionSet, UUID> {
}
