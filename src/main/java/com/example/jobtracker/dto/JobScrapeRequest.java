package com.example.jobtracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class JobScrapeRequest {

    @NotNull(message = "Job preference id is required")
    @Positive(message = "Job preference id must be positive")
    private Long jobPreferenceId;

    public Long getJobPreferenceId() {
        return jobPreferenceId;
    }

    public void setJobPreferenceId(Long jobPreferenceId) {
        this.jobPreferenceId = jobPreferenceId;
    }
}
