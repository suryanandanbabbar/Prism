package com.example.jobtracker.service;

import com.example.jobtracker.dto.JobApplicationRequest;
import com.example.jobtracker.dto.JobApplicationResponse;
import com.example.jobtracker.dto.JobApplicationTimelineResponse;
import com.example.jobtracker.entity.ApplicationStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobApplicationService {

    JobApplicationResponse create(JobApplicationRequest request);

    JobApplicationResponse update(Long id, JobApplicationRequest request);

    JobApplicationResponse getById(Long id);

    List<JobApplicationResponse> getAll(ApplicationStatus status);

    Page<JobApplicationResponse> search(ApplicationStatus status, String company, LocalDate fromDate, LocalDate toDate,
            String keyword, Pageable pageable);

    List<JobApplicationTimelineResponse> getTimeline(Long id);

    String exportCsv(ApplicationStatus status, String company, LocalDate fromDate, LocalDate toDate, String keyword);

    void delete(Long id);
}
