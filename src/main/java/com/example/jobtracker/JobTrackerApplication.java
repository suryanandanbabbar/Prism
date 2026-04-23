package com.example.jobtracker;

import com.example.jobtracker.config.FileStorageProperties;
import com.example.jobtracker.config.ResumeProcessingProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class, ResumeProcessingProperties.class})
public class JobTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobTrackerApplication.class, args);
    }
}
