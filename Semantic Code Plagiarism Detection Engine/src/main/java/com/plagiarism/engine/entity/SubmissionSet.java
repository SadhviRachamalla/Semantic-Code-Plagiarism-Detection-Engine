package com.plagiarism.engine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "submission_sets")
@Getter
@Setter
public class SubmissionSet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "submissionSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>();
}
