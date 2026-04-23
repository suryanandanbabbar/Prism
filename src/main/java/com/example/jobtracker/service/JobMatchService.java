package com.example.jobtracker.service;

import com.example.jobtracker.entity.JobApplication;
import com.example.jobtracker.entity.JobPreference;

public interface JobMatchService {

    double score(JobPreference preference, JobApplication application);
}
