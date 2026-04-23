package com.example.jobtracker.repository;

import com.example.jobtracker.entity.ApplicationStatus;
import com.example.jobtracker.entity.JobApplication;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByStatus(ApplicationStatus status);

    List<JobApplication> findByStatusOrderByAppliedDateDesc(ApplicationStatus status);

    boolean existsByJobHash(String jobHash);
}
