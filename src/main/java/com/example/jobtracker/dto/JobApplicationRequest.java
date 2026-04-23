package com.example.jobtracker.dto;

import com.example.jobtracker.entity.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class JobApplicationRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 160, message = "Company name must be at most 160 characters")
    private String companyName;

    @NotBlank(message = "Role is required")
    @Size(max = 120, message = "Role must be at most 120 characters")
    private String role;

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    @NotNull(message = "Applied date is required")
    @PastOrPresent(message = "Applied date cannot be in the future")
    private LocalDate appliedDate;

    @NotBlank(message = "Source is required")
    @Size(max = 120, message = "Source must be at most 120 characters")
    private String source;

    @Size(max = 1000, message = "Job link must be at most 1000 characters")
    private String jobLink;

    @Size(max = 10000, message = "Job description must be at most 10000 characters")
    private String jobDescription;

    @NotBlank(message = "Resume version is required")
    @Size(max = 80, message = "Resume version must be at most 80 characters")
    private String resumeVersion;

    @Size(max = 5000, message = "Notes must be at most 5000 characters")
    private String notes;

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
