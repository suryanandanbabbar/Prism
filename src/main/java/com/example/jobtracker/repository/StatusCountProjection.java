package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationStatus;

public interface StatusCountProjection {

    ApplicationStatus getStatus();

    Long getCount();
}
