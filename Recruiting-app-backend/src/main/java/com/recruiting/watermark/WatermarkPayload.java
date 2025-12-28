package com.recruiting.watermark;

import java.io.Serializable;
import java.time.LocalDateTime;

public class WatermarkPayload implements Serializable {
    private Long applicationId;
    private Long candidateId;
    private Long jobId;
    private Long recruiterId;
    private LocalDateTime timestamp;
    private String uniqueHash; // SHA-256 of the combination to verify integrity

    // Constructors, Getters, Setters
    public WatermarkPayload() {
    }

    public WatermarkPayload(Long applicationId, Long candidateId, Long jobId, Long recruiterId, LocalDateTime timestamp,
            String uniqueHash) {
        this.applicationId = applicationId;
        this.candidateId = candidateId;
        this.jobId = jobId;
        this.recruiterId = recruiterId;
        this.timestamp = timestamp;
        this.uniqueHash = uniqueHash;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getRecruiterId() {
        return recruiterId;
    }

    public void setRecruiterId(Long recruiterId) {
        this.recruiterId = recruiterId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUniqueHash() {
        return uniqueHash;
    }

    public void setUniqueHash(String uniqueHash) {
        this.uniqueHash = uniqueHash;
    }

    @Override
    public String toString() {
        return "WatermarkPayload{" +
                "applicationId=" + applicationId +
                ", recruiterId=" + recruiterId +
                ", timestamp=" + timestamp +
                '}';
    }
}
