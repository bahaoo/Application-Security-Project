-- Database Schema for Recruitment App

-- Users Table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('CANDIDATE', 'RECRUITER', 'ADMIN'))
);

-- Jobs Table
CREATE TABLE jobs (
    id SERIAL PRIMARY KEY,
    recruiter_id INTEGER NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Applications Table
CREATE TABLE applications (
    id SERIAL PRIMARY KEY,
    candidate_id INTEGER NOT NULL REFERENCES users(id),
    job_id INTEGER NOT NULL REFERENCES jobs(id),
    cv_path VARCHAR(512) NOT NULL, -- Path to encrypted CV file
    watermark_hash VARCHAR(255), -- Hash of the original watermark payload
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'APPLIED' -- APPLIED, VIEWED, etc.
);

-- Access Logs (For security auditing)
CREATE TABLE access_logs (
    id SERIAL PRIMARY KEY,
    recruiter_id INTEGER NOT NULL REFERENCES users(id),
    application_id INTEGER NOT NULL REFERENCES applications(id),
    access_type VARCHAR(50) NOT NULL, -- VIEW_CV, DOWNLOAD_CV
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50)
);

-- Indices for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_jobs_recruiter ON jobs(recruiter_id);
CREATE INDEX idx_applications_job ON applications(job_id);
