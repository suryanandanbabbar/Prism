package com.example.jobtracker.scheduler;

import com.example.jobtracker.config.JobScraperProperties;
import com.example.jobtracker.service.JobScraperService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScraperScheduler {

    private final JobScraperProperties jobScraperProperties;
    private final JobScraperService jobScraperService;

    public JobScraperScheduler(JobScraperProperties jobScraperProperties, JobScraperService jobScraperService) {
        this.jobScraperProperties = jobScraperProperties;
        this.jobScraperService = jobScraperService;
    }

    @Scheduled(fixedDelayString = "PT6H", initialDelayString = "PT1M")
    public void scrapeEverySixHours() {
        if (jobScraperProperties.isSchedulerEnabled()) {
            jobScraperService.scrapeAllPreferences();
        }
    }
}
