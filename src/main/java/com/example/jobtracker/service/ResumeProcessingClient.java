package com.example.jobtracker.service;

import com.example.jobtracker.dto.ParsedResumeResponse;
import com.example.jobtracker.dto.ResumeOptimizerClientResponse;
import java.nio.file.Path;

public interface ResumeProcessingClient {

    ParsedResumeResponse parse(Path resumePath);

    ResumeOptimizerClientResponse optimize(String jobDescription, String originalResume);
}
