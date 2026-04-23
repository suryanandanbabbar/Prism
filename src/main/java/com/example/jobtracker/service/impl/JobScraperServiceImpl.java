package com.example.jobtracker.service.impl;

import com.example.jobtracker.config.AutoApplyProperties;
import com.example.jobtracker.dto.JobApplicationResponse;
import com.example.jobtracker.dto.JobScrapeResultResponse;
import com.example.jobtracker.dto.JobScraperClientRequest;
import com.example.jobtracker.dto.JobScraperClientResponse;
import com.example.jobtracker.dto.ScrapedJobResponse;
import com.example.jobtracker.entity.ApplicationStatus;
import com.example.jobtracker.entity.JobApplication;
import com.example.jobtracker.entity.JobApplicationStatusHistory;
import com.example.jobtracker.entity.JobPreference;
import com.example.jobtracker.entity.ResumeVersion;
import com.example.jobtracker.exception.ResourceNotFoundException;
import com.example.jobtracker.repository.JobApplicationRepository;
import com.example.jobtracker.repository.JobApplicationStatusHistoryRepository;
import com.example.jobtracker.repository.JobPreferenceRepository;
import com.example.jobtracker.repository.ResumeVersionRepository;
import com.example.jobtracker.service.JobScraperClient;
import com.example.jobtracker.service.JobMatchService;
import com.example.jobtracker.service.JobScraperService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class JobScraperServiceImpl implements JobScraperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobScraperServiceImpl.class);

    private final JobPreferenceRepository jobPreferenceRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final JobApplicationStatusHistoryRepository historyRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final JobScraperClient jobScraperClient;
    private final JobMatchService jobMatchService;
    private final AutoApplyProperties autoApplyProperties;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    public JobScraperServiceImpl(JobPreferenceRepository jobPreferenceRepository,
            JobApplicationRepository jobApplicationRepository,
            JobApplicationStatusHistoryRepository historyRepository,
            ResumeVersionRepository resumeVersionRepository,
            JobScraperClient jobScraperClient,
            JobMatchService jobMatchService,
            AutoApplyProperties autoApplyProperties,
            ObjectMapper objectMapper,
            ModelMapper modelMapper) {
        this.jobPreferenceRepository = jobPreferenceRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.historyRepository = historyRepository;
        this.resumeVersionRepository = resumeVersionRepository;
        this.jobScraperClient = jobScraperClient;
        this.jobMatchService = jobMatchService;
        this.autoApplyProperties = autoApplyProperties;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public JobScrapeResultResponse scrapeByPreferenceId(Long jobPreferenceId) {
        JobPreference preference = jobPreferenceRepository.findById(jobPreferenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job preference not found with id: " + jobPreferenceId));
        return scrapePreference(preference);
    }

    @Override
    public void scrapeAllPreferences() {
        jobPreferenceRepository.findAll().forEach(preference -> {
            try {
                scrapePreference(preference);
            } catch (RuntimeException exception) {
                // Scheduler should continue processing remaining preferences when one scrape fails.
                LOGGER.warn("Scheduled scrape failed for job preference id {}", preference.getId(), exception);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getDiscoveredJobs() {
        return jobApplicationRepository.findByStatusOrderByAppliedDateDesc(ApplicationStatus.DISCOVERED).stream()
                .map(application -> modelMapper.map(application, JobApplicationResponse.class))
                .toList();
    }

    private JobScrapeResultResponse scrapePreference(JobPreference preference) {
        JobScraperClientRequest request = new JobScraperClientRequest(
                preference.getRole(),
                preference.getLocation(),
                parseSkills(preference.getSkills()));
        JobScraperClientResponse scraperResponse = jobScraperClient.scrape(request);

        JobScrapeResultResponse result = new JobScrapeResultResponse();
        if (scraperResponse == null || scraperResponse.getJobs() == null) {
            result.getErrors().add("Job scraper service returned an empty response");
            return result;
        }

        result.setFetchedCount(scraperResponse.getJobs().size());
        if (scraperResponse.getErrors() != null) {
            result.getErrors().addAll(scraperResponse.getErrors());
        }

        for (ScrapedJobResponse scrapedJob : scraperResponse.getJobs()) {
            if (!isValid(scrapedJob)) {
                result.getErrors().add("Skipped invalid scraped job from source: " + scrapedJob.getSource());
                continue;
            }

            String jobHash = createHash(scrapedJob.getCompany(), scrapedJob.getRole(), scrapedJob.getLink());
            if (jobApplicationRepository.existsByJobHash(jobHash)) {
                result.setDuplicateCount(result.getDuplicateCount() + 1);
                continue;
            }

            JobApplication application = new JobApplication();
            application.setCompanyName(scrapedJob.getCompany());
            application.setRole(scrapedJob.getRole());
            application.setStatus(ApplicationStatus.DISCOVERED);
            application.setAppliedDate(LocalDate.now());
            application.setSource(scrapedJob.getSource());
            application.setJobLink(scrapedJob.getLink());
            application.setJobDescription(scrapedJob.getDescription());
            application.setJobHash(jobHash);
            application.setResumeVersion("N/A");
            application.setNotes("Discovered by scraper");
            evaluateForApproval(preference, application);

            JobApplication savedApplication = jobApplicationRepository.save(application);
            if (savedApplication.getStatus() == ApplicationStatus.PENDING_APPROVAL) {
                recordHistory(savedApplication, null, ApplicationStatus.DISCOVERED, "Scraper discovered job");
                recordHistory(savedApplication, ApplicationStatus.DISCOVERED, ApplicationStatus.PENDING_APPROVAL,
                        "Match threshold passed");
            } else {
                recordHistory(savedApplication, null, savedApplication.getStatus(), "Scraper discovered job");
            }
            result.getSavedJobs().add(modelMapper.map(savedApplication, JobApplicationResponse.class));
            result.setSavedCount(result.getSavedCount() + 1);
        }
        return result;
    }

    private List<String> parseSkills(String skills) {
        if (!StringUtils.hasText(skills)) {
            return List.of();
        }
        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }

    private boolean isValid(ScrapedJobResponse job) {
        return job != null
                && StringUtils.hasText(job.getCompany())
                && StringUtils.hasText(job.getRole())
                && StringUtils.hasText(job.getLink())
                && StringUtils.hasText(job.getSource());
    }

    private String createHash(String company, String role, String link) {
        String source = normalize(company) + "|" + normalize(role) + "|" + normalize(link);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(source.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", exception);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
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

    private void evaluateForApproval(JobPreference preference, JobApplication application) {
        double matchScore = jobMatchService.score(preference, application);
        application.setMatchScore(matchScore);
        if (matchScore <= autoApplyProperties.getMatchThreshold()) {
            return;
        }

        Optional<ResumeVersion> resumeVersion = resumeVersionRepository.findTopByCvDocumentUserIdOrderByCreatedAtDesc(
                preference.getUser().getId());
        if (resumeVersion.isEmpty()) {
            application.setNotes("Discovered by scraper; match threshold passed but no resume version is available");
            return;
        }

        ResumeVersion suggestedVersion = resumeVersion.get();
        application.setStatus(ApplicationStatus.PENDING_APPROVAL);
        application.setSuggestedResumeVersionId(suggestedVersion.getId());
        application.setResumeVersion(suggestedVersion.getVersionLabel());
        application.setApplicationPayload(createApplicationPayload(preference, application, suggestedVersion));
        application.setNotes("Pending user approval before auto apply");
    }

    private String createApplicationPayload(JobPreference preference, JobApplication application,
            ResumeVersion resumeVersion) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "company", application.getCompanyName(),
                    "role", application.getRole(),
                    "source", application.getSource(),
                    "jobLink", application.getJobLink(),
                    "matchScore", application.getMatchScore(),
                    "preferenceId", preference.getId(),
                    "resumeVersionId", resumeVersion.getId(),
                    "resumeVersionLabel", resumeVersion.getVersionLabel()));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not create application payload", exception);
        }
    }
}
