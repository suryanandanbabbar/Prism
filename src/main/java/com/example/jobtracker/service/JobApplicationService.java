package com.example.jobtracker.service;

import com.example.jobtracker.dto.JobApplicationRequest;
import com.example.jobtracker.dto.JobApplicationResponse;
import com.example.jobtracker.entity.ApplicationStatus;
import java.util.List;

public interface JobApplicationService {

    JobApplicationResponse create(JobApplicationRequest request);

    JobApplicationResponse update(Long id, JobApplicationRequest request);

    JobApplicationResponse getById(Long id);

    List<JobApplicationResponse> getAll(ApplicationStatus status);

    void delete(Long id);
}
