package com.example.jobtracker.dto;

import java.util.ArrayList;
import java.util.List;

public class JobScraperClientResponse {

    private List<ScrapedJobResponse> jobs = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public List<ScrapedJobResponse> getJobs() {
        return jobs;
    }

    public void setJobs(List<ScrapedJobResponse> jobs) {
        this.jobs = jobs;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
