package com.plagiarism.engine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DbFingerprint {
    @Column(name = "hash_value", nullable = false)
    private long hashValue;

    @Column(name = "position", nullable = false)
    private int position;
}
