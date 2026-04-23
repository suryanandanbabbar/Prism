package com.example.jobtracker.dto;

import java.util.ArrayList;
import java.util.List;

public class JobScraperClientRequest {

    private String role;
    private String location;
    private List<String> skills = new ArrayList<>();

    public JobScraperClientRequest() {
    }

    public JobScraperClientRequest(String role, String location, List<String> skills) {
        this.role = role;
        this.location = location;
        this.skills = skills;
    }

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

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
