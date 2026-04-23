package com.example.jobtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResumeOptimizerClientRequest {

    @JsonProperty("job_description")
    private String jobDescription;

    @JsonProperty("original_resume")
    private String originalResume;

    public ResumeOptimizerClientRequest() {
    }

    public ResumeOptimizerClientRequest(String jobDescription, String originalResume) {
        this.jobDescription = jobDescription;
        this.originalResume = originalResume;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getOriginalResume() {
        return originalResume;
    }

    public void setOriginalResume(String originalResume) {
        this.originalResume = originalResume;
    }
}
