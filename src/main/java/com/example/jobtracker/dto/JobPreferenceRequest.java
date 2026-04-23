package com.example.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class JobPreferenceRequest {

    @NotBlank(message = "Role is required")
    @Size(max = 120, message = "Role must be at most 120 characters")
    private String role;

    @NotBlank(message = "Location is required")
    @Size(max = 120, message = "Location must be at most 120 characters")
    private String location;

    @NotBlank(message = "Skills are required")
    @Size(max = 500, message = "Skills must be at most 500 characters")
    private String skills;

    @NotBlank(message = "Salary range is required")
    @Size(max = 80, message = "Salary range must be at most 80 characters")
    private String salaryRange;

    @NotNull(message = "User id is required")
    @Positive(message = "User id must be positive")
    private Long userId;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
