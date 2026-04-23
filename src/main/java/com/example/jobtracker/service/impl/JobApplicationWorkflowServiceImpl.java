package com.example.jobtracker.service.impl;

import com.example.jobtracker.config.AutoApplyProperties;
import com.example.jobtracker.dto.ApplicationAutomationLogResponse;
import com.example.jobtracker.dto.AutoApplyClientRequest;
import com.example.jobtracker.dto.AutoApplyClientResponse;
import com.example.jobtracker.dto.JobApplicationResponse;
import com.example.jobtracker.entity.ApplicationAutomationLog;
import com.example.jobtracker.entity.ApplicationStatus;
import com.example.jobtracker.entity.JobApplication;
import com.example.jobtracker.entity.JobApplicationStatusHistory;
import com.example.jobtracker.entity.ResumeVersion;
import com.example.jobtracker.exception.ExternalServiceException;
import com.example.jobtracker.exception.InvalidWorkflowException;
import com.example.jobtracker.exception.ResourceNotFoundException;
import com.example.jobtracker.repository.ApplicationAutomationLogRepository;
import com.example.jobtracker.repository.JobApplicationRepository;
import com.example.jobtracker.repository.JobApplicationStatusHistoryRepository;
import com.example.jobtracker.repository.ResumeVersionRepository;
import com.example.jobtracker.service.AutoApplyClient;
import com.example.jobtracker.service.JobApplicationWorkflowService;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobApplicationWorkflowServiceImpl implements JobApplicationWorkflowService {

    private final JobApplicationRepository jobApplicationRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final ApplicationAutomationLogRepository logRepository;
    private final JobApplicationStatusHistoryRepository historyRepository;
    private final AutoApplyClient autoApplyClient;
    private final AutoApplyProperties autoApplyProperties;
    private final ModelMapper modelMapper;

    public JobApplicationWorkflowServiceImpl(JobApplicationRepository jobApplicationRepository,
            ResumeVersionRepository resumeVersionRepository,
            ApplicationAutomationLogRepository logRepository,
            JobApplicationStatusHistoryRepository historyRepository,
            AutoApplyClient autoApplyClient,
            AutoApplyProperties autoApplyProperties,
            ModelMapper modelMapper) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.resumeVersionRepository = resumeVersionRepository;
        this.logRepository = logRepository;
        this.historyRepository = historyRepository;
        this.autoApplyClient = autoApplyClient;
        this.autoApplyProperties = autoApplyProperties;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getPendingApplications() {
        return jobApplicationRepository.findByStatusOrderByAppliedDateDesc(ApplicationStatus.PENDING_APPROVAL).stream()
                .map(application -> modelMapper.map(application, JobApplicationResponse.class))
                .toList();
    }

    @Override
    public JobApplicationResponse approve(Long applicationId) {
        JobApplication application = getApplication(applicationId);
        assertPending(application);
        if (isPlatformDisabled(application.getSource())) {
            log(application, "APPROVE", "FAILED", "Automation is disabled for platform: " + application.getSource());
            throw new InvalidWorkflowException("Automation is disabled for platform: " + application.getSource());
        }

        if (application.getSuggestedResumeVersionId() == null) {
            log(application, "APPROVE", "FAILED", "No suggested resume version is attached to this application");
            throw new InvalidWorkflowException("No suggested resume version is attached to this application");
        }

        ResumeVersion resumeVersion = resumeVersionRepository.findById(application.getSuggestedResumeVersionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Suggested resume version not found for application id: " + applicationId));

        AutoApplyClientRequest request = new AutoApplyClientRequest();
        request.setJobApplicationId(application.getId());
        request.setCompany(application.getCompanyName());
        request.setRole(application.getRole());
        request.setJobLink(application.getJobLink());
        request.setSource(application.getSource());
        request.setResumeFilePath(resumeVersion.getCvDocument().getFilePath());
        request.setPayload(application.getApplicationPayload());

        AutoApplyClientResponse response;
        try {
            response = autoApplyClient.apply(request);
        } catch (ExternalServiceException exception) {
            log(application, "APPROVE", "FAILED", exception.getMessage());
            throw exception;
        }
        if (response != null && response.isSuccess()) {
            ApplicationStatus previousStatus = application.getStatus();
            application.setStatus(ApplicationStatus.APPLIED);
            application.setAppliedDate(LocalDate.now());
            recordHistory(application, previousStatus, ApplicationStatus.APPLIED, "User approved auto apply");
            log(application, "APPROVE", "SUCCESS", response.getMessage());
            return modelMapper.map(jobApplicationRepository.save(application), JobApplicationResponse.class);
        }
        String message = response == null ? "Auto apply service returned an empty response" : response.getMessage();
        log(application, "APPROVE", "FAILED", message);
        throw new ExternalServiceException(message);
    }

    @Override
    public JobApplicationResponse reject(Long applicationId) {
        JobApplication application = getApplication(applicationId);
        assertPending(application);
        ApplicationStatus previousStatus = application.getStatus();
        application.setStatus(ApplicationStatus.REJECTED);
        recordHistory(application, previousStatus, ApplicationStatus.REJECTED, "User rejected pending application");
        log(application, "REJECT", "SUCCESS", "Application rejected by user before automation");
        return modelMapper.map(jobApplicationRepository.save(application), JobApplicationResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationAutomationLogResponse> getLogs(Long applicationId) {
        if (!jobApplicationRepository.existsById(applicationId)) {
            throw new ResourceNotFoundException("Job application not found with id: " + applicationId);
        }
        return logRepository.findByJobApplicationIdOrderByCreatedAtDesc(applicationId).stream()
                .map(this::toLogResponse)
                .toList();
    }

    private JobApplication getApplication(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));
    }

    private void assertPending(JobApplication application) {
        if (application.getStatus() != ApplicationStatus.PENDING_APPROVAL) {
            throw new InvalidWorkflowException("Application must be PENDING_APPROVAL before this action");
        }
    }

    private boolean isPlatformDisabled(String source) {
        return autoApplyProperties.getDisabledPlatforms().stream()
                .map(platform -> platform.toLowerCase(Locale.ROOT))
                .anyMatch(platform -> platform.equals(source.toLowerCase(Locale.ROOT)));
    }

    private void log(JobApplication application, String action, String status, String message) {
        ApplicationAutomationLog automationLog = new ApplicationAutomationLog();
        automationLog.setJobApplication(application);
        automationLog.setAction(action);
        automationLog.setStatus(status);
        automationLog.setMessage(message);
        automationLog.setCreatedAt(OffsetDateTime.now());
        logRepository.save(automationLog);
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

    private ApplicationAutomationLogResponse toLogResponse(ApplicationAutomationLog log) {
        ApplicationAutomationLogResponse response = modelMapper.map(log, ApplicationAutomationLogResponse.class);
        response.setJobApplicationId(log.getJobApplication().getId());
        return response;
    }
}
