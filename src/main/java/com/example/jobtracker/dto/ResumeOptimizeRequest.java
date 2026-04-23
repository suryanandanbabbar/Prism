package com.example.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ResumeOptimizeRequest {

    @NotNull(message = "CV document id is required")
    @Positive(message = "CV document id must be positive")
    private Long cvDocumentId;

    @NotBlank(message = "Job description is required")
    @Size(max = 20000, message = "Job description must be at most 20000 characters")
    private String jobDescription;

    @Size(max = 80, message = "Version label must be at most 80 characters")
    private String versionLabel;

    public Long getCvDocumentId() {
        return cvDocumentId;
    }

    public void setCvDocumentId(Long cvDocumentId) {
        this.cvDocumentId = cvDocumentId;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getVersionLabel() {
        return versionLabel;
    }

    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }
}
