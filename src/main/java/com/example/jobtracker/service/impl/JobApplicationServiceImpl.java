package com.example.jobtracker.service.impl;

import com.example.jobtracker.dto.JobApplicationRequest;
import com.example.jobtracker.dto.JobApplicationResponse;
import com.example.jobtracker.dto.JobApplicationTimelineResponse;
import com.example.jobtracker.entity.ApplicationStatus;
import com.example.jobtracker.entity.JobApplication;
import com.example.jobtracker.entity.JobApplicationStatusHistory;
import com.example.jobtracker.exception.ResourceNotFoundException;
import com.example.jobtracker.repository.JobApplicationRepository;
import com.example.jobtracker.repository.JobApplicationStatusHistoryRepository;
import com.example.jobtracker.service.JobApplicationService;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobApplicationStatusHistoryRepository historyRepository;
    private final ModelMapper modelMapper;

    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository,
            JobApplicationStatusHistoryRepository historyRepository,
            ModelMapper modelMapper) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.historyRepository = historyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public JobApplicationResponse create(JobApplicationRequest request) {
        JobApplication application = modelMapper.map(request, JobApplication.class);
        application.setId(null);
        JobApplication savedApplication = jobApplicationRepository.save(application);
        recordHistory(savedApplication, null, savedApplication.getStatus(), "Application created");
        return toResponse(savedApplication);
    }

    @Override
    public JobApplicationResponse update(Long id, JobApplicationRequest request) {
        JobApplication application = getApplication(id);
        ApplicationStatus previousStatus = application.getStatus();
        application.setCompanyName(request.getCompanyName());
        application.setRole(request.getRole());
        application.setStatus(request.getStatus());
        application.setAppliedDate(request.getAppliedDate());
        application.setSource(request.getSource());
        application.setJobLink(request.getJobLink());
        application.setJobDescription(request.getJobDescription());
        application.setMatchScore(request.getMatchScore());
        application.setSuggestedResumeVersionId(request.getSuggestedResumeVersionId());
        application.setApplicationPayload(request.getApplicationPayload());
        application.setResumeVersion(request.getResumeVersion());
        application.setNotes(request.getNotes());
        JobApplication savedApplication = jobApplicationRepository.save(application);
        if (previousStatus != savedApplication.getStatus()) {
            recordHistory(savedApplication, previousStatus, savedApplication.getStatus(), "Application updated");
        }
        return toResponse(savedApplication);
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplicationResponse getById(Long id) {
        return toResponse(getApplication(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getAll(ApplicationStatus status) {
        List<JobApplication> applications = status == null
                ? jobApplicationRepository.findAll()
                : jobApplicationRepository.findByStatus(status);
        return applications.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> search(ApplicationStatus status, String company, LocalDate fromDate,
            LocalDate toDate, String keyword, Pageable pageable) {
        return jobApplicationRepository.searchApplications(status, clean(company), fromDate, toDate, clean(keyword),
                pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationTimelineResponse> getTimeline(Long id) {
        if (!jobApplicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Job application not found with id: " + id);
        }
        return historyRepository.findByJobApplicationIdOrderByChangedAtDesc(id).stream()
                .map(this::toTimelineResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String exportCsv(ApplicationStatus status, String company, LocalDate fromDate, LocalDate toDate,
            String keyword) {
        List<JobApplicationResponse> applications = jobApplicationRepository.searchApplications(status, clean(company),
                        fromDate, toDate, clean(keyword), Pageable.unpaged())
                .map(this::toResponse)
                .toList();

        StringBuilder csv = new StringBuilder("id,companyName,role,status,appliedDate,source,jobLink,matchScore\n");
        applications.forEach(application -> csv.append(application.getId()).append(',')
                .append(escapeCsv(application.getCompanyName())).append(',')
                .append(escapeCsv(application.getRole())).append(',')
                .append(application.getStatus()).append(',')
                .append(application.getAppliedDate()).append(',')
                .append(escapeCsv(application.getSource())).append(',')
                .append(escapeCsv(application.getJobLink())).append(',')
                .append(application.getMatchScore() == null ? "" : application.getMatchScore())
                .append('\n'));
        return csv.toString();
    }

    @Override
    public void delete(Long id) {
        JobApplication application = getApplication(id);
        jobApplicationRepository.delete(application);
    }

    private JobApplication getApplication(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));
    }

    private void recordHistory(JobApplication application, ApplicationStatus fromStatus, ApplicationStatus toStatus,
            String reason) {
        JobApplicationStatusHistory history = new JobApplicationStatusHistory();
        history.setJobApplication(application);
        history.setFromStatus(fromStatus);
        history.setToStatus(toStatus);
        history.setReason(reason);
        history.setChangedAt(OffsetDateTime.now());
        historyRepository.save(history);
    }

    private JobApplicationResponse toResponse(JobApplication application) {
        return modelMapper.map(application, JobApplicationResponse.class);
    }

    private JobApplicationTimelineResponse toTimelineResponse(JobApplicationStatusHistory history) {
        JobApplicationTimelineResponse response = modelMapper.map(history, JobApplicationTimelineResponse.class);
        response.setJobApplicationId(history.getJobApplication().getId());
        return response;
    }

    private String clean(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
