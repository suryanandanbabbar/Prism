package com.example.jobtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AutoApplyClientRequest {

    @JsonProperty("job_application_id")
    private Long jobApplicationId;

    @JsonProperty("company")
    private String company;

    @JsonProperty("role")
    private String role;

    @JsonProperty("job_link")
    private String jobLink;

    @JsonProperty("source")
    private String source;

    @JsonProperty("resume_file_path")
    private String resumeFilePath;

    @JsonProperty("payload")
    private String payload;

    public Long getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getJobLink() {
        return jobLink;
    }

    public void setJobLink(String jobLink) {
        this.jobLink = jobLink;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getResumeFilePath() {
        return resumeFilePath;
    }

    public void setResumeFilePath(String resumeFilePath) {
        this.resumeFilePath = resumeFilePath;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
