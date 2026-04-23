package com.example.jobtracker.dto;

import java.util.ArrayList;
import java.util.List;

public class JobScrapeResultResponse {

    private int fetchedCount;
    private int savedCount;
    private int duplicateCount;
    private List<JobApplicationResponse> savedJobs = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public int getFetchedCount() {
        return fetchedCount;
    }

    public void setFetchedCount(int fetchedCount) {
        this.fetchedCount = fetchedCount;
    }

    public int getSavedCount() {
        return savedCount;
    }

    public void setSavedCount(int savedCount) {
        this.savedCount = savedCount;
    }

    public int getDuplicateCount() {
        return duplicateCount;
    }

    public void setDuplicateCount(int duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

    public List<JobApplicationResponse> getSavedJobs() {
        return savedJobs;
    }

    public void setSavedJobs(List<JobApplicationResponse> savedJobs) {
        this.savedJobs = savedJobs;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
