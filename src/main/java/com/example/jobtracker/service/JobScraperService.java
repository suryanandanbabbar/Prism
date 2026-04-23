package com.example.jobtracker.service;

import com.example.jobtracker.dto.JobApplicationResponse;
import com.example.jobtracker.dto.JobScrapeResultResponse;
import java.util.List;

public interface JobScraperService {

    JobScrapeResultResponse scrapeByPreferenceId(Long jobPreferenceId);

    void scrapeAllPreferences();

    List<JobApplicationResponse> getDiscoveredJobs();
}
