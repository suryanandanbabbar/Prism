package com.example.jobtracker.controller;

import com.example.jobtracker.dto.JobApplicationResponse;
import com.example.jobtracker.dto.JobScrapeRequest;
import com.example.jobtracker.dto.JobScrapeResultResponse;
import com.example.jobtracker.service.JobScraperService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobDiscoveryController {

    private final JobScraperService jobScraperService;

    public JobDiscoveryController(JobScraperService jobScraperService) {
        this.jobScraperService = jobScraperService;
    }

    @GetMapping("/discovered")
    public ResponseEntity<List<JobApplicationResponse>> getDiscoveredJobs() {
        return ResponseEntity.ok(jobScraperService.getDiscoveredJobs());
    }

    @PostMapping("/scrape")
    public ResponseEntity<JobScrapeResultResponse> scrapeJobs(@Valid @RequestBody JobScrapeRequest request) {
        return ResponseEntity.ok(jobScraperService.scrapeByPreferenceId(request.getJobPreferenceId()));
    }
}
