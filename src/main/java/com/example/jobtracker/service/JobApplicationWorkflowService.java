package com.example.jobtracker.service;

import com.example.jobtracker.dto.ApplicationAutomationLogResponse;
import com.example.jobtracker.dto.JobApplicationResponse;
import java.util.List;

public interface JobApplicationWorkflowService {

    List<JobApplicationResponse> getPendingApplications();

    JobApplicationResponse approve(Long applicationId);

    JobApplicationResponse reject(Long applicationId);

    List<ApplicationAutomationLogResponse> getLogs(Long applicationId);
}
