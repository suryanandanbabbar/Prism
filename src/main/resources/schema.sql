CREATE TABLE IF NOT EXISTS app_users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS job_preferences (
    id BIGSERIAL PRIMARY KEY,
    role VARCHAR(120) NOT NULL,
    location VARCHAR(120) NOT NULL,
    skills VARCHAR(500) NOT NULL,
    salary_range VARCHAR(80) NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_job_preferences_user
        FOREIGN KEY (user_id)
        REFERENCES app_users (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS job_applications (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(160) NOT NULL,
    role VARCHAR(120) NOT NULL,
    status VARCHAR(30) NOT NULL,
    applied_date DATE NOT NULL,
    source VARCHAR(120) NOT NULL,
    resume_version VARCHAR(80) NOT NULL,
    notes TEXT,
    CONSTRAINT chk_job_applications_status
        CHECK (status IN ('APPLIED', 'INTERVIEW', 'REJECTED', 'OFFER'))
);

CREATE TABLE IF NOT EXISTS cv_documents (
    id BIGSERIAL PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL UNIQUE,
    content_type VARCHAR(120),
    file_size BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_cv_documents_user
        FOREIGN KEY (user_id)
        REFERENCES app_users (id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_job_preferences_user_id ON job_preferences (user_id);
CREATE INDEX IF NOT EXISTS idx_job_applications_status ON job_applications (status);
CREATE INDEX IF NOT EXISTS idx_cv_documents_user_id ON cv_documents (user_id);
