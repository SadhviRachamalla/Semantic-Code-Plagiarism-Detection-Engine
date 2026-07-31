-- Create submission sets table
CREATE TABLE submission_sets (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create submissions table
CREATE TABLE submissions (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    language VARCHAR(50) NOT NULL,
    source_code TEXT NOT NULL,
    normalized_tokens TEXT NOT NULL,
    file_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submission_set_id UUID REFERENCES submission_sets(id) ON DELETE SET NULL
);

-- Create index on file_hash for duplicate lookup optimization
CREATE INDEX idx_submissions_file_hash ON submissions(file_hash);

-- Create submission fingerprints collection table
CREATE TABLE submission_fingerprints (
    submission_id UUID NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
    hash_value BIGINT NOT NULL,
    position INT NOT NULL,
    fingerprint_order INT NOT NULL,
    PRIMARY KEY (submission_id, fingerprint_order)
);

-- Create index on hash_value to allow fast database searches/comparisons
CREATE INDEX idx_fingerprints_hash_value ON submission_fingerprints(hash_value);

-- Create comparison results table
CREATE TABLE comparison_results (
    id UUID PRIMARY KEY,
    submission_a_id UUID NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
    submission_b_id UUID NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
    similarity_score DOUBLE PRECISION NOT NULL,
    winnowing_score DOUBLE PRECISION NOT NULL,
    cosine_score DOUBLE PRECISION NOT NULL,
    lcs_score DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_submission_pair UNIQUE (submission_a_id, submission_b_id)
);

-- Create index on similarity_score for reports
CREATE INDEX idx_comparison_similarity ON comparison_results(similarity_score);

-- Create audit logs table
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    details TEXT NOT NULL,
    performed_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
