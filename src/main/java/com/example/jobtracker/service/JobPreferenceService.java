package com.example.jobtracker.service;

import com.example.jobtracker.dto.JobPreferenceRequest;
import com.example.jobtracker.dto.JobPreferenceResponse;
import java.util.List;

public interface JobPreferenceService {

    JobPreferenceResponse create(JobPreferenceRequest request);

    JobPreferenceResponse update(Long id, JobPreferenceRequest request);

    JobPreferenceResponse getById(Long id);

    List<JobPreferenceResponse> getByUserId(Long userId);
}
