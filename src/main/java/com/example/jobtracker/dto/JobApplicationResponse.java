package com.example.jobtracker.dto;

import com.example.jobtracker.entity.ApplicationStatus;
import java.time.LocalDate;

public class JobApplicationResponse {

    private Long id;
    private String companyName;
    private String role;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private String source;
    private String jobLink;
    private String jobDescription;
    private String jobHash;
    private Double matchScore;
    private Long suggestedResumeVersionId;
    private String applicationPayload;
    private String resumeVersion;
    private String notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getJobLink() {
        return jobLink;
    }

    public void setJobLink(String jobLink) {
        this.jobLink = jobLink;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobHash() {
        return jobHash;
    }

    public void setJobHash(String jobHash) {
        this.jobHash = jobHash;
    }

    public Double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Double matchScore) {
        this.matchScore = matchScore;
    }

    public Long getSuggestedResumeVersionId() {
        return suggestedResumeVersionId;
    }

    public void setSuggestedResumeVersionId(Long suggestedResumeVersionId) {
        this.suggestedResumeVersionId = suggestedResumeVersionId;
    }

    public String getApplicationPayload() {
        return applicationPayload;
    }

    public void setApplicationPayload(String applicationPayload) {
        this.applicationPayload = applicationPayload;
    }

    public String getResumeVersion() {
        return resumeVersion;
    }

    public void setResumeVersion(String resumeVersion) {
        this.resumeVersion = resumeVersion;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
