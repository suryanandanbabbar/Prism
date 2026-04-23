package com.example.jobtracker.service;

import com.example.jobtracker.dto.JobScraperClientRequest;
import com.example.jobtracker.dto.JobScraperClientResponse;

public interface JobScraperClient {

    JobScraperClientResponse scrape(JobScraperClientRequest request);
}
