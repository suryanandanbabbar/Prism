package com.example.jobtracker;

import com.example.jobtracker.config.FileStorageProperties;
import com.example.jobtracker.config.JobScraperProperties;
import com.example.jobtracker.config.ResumeProcessingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({FileStorageProperties.class, ResumeProcessingProperties.class, JobScraperProperties.class})
public class JobTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobTrackerApplication.class, args);
    }
}
