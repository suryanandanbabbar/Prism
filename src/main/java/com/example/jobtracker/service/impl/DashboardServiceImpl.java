package com.example.jobtracker.service.impl;

import com.example.jobtracker.dto.DashboardStatsResponse;
import com.example.jobtracker.entity.ApplicationStatus;
import com.example.jobtracker.repository.JobApplicationRepository;
import com.example.jobtracker.repository.StatusCountProjection;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements com.example.jobtracker.service.DashboardService {

    private final JobApplicationRepository jobApplicationRepository;

    public DashboardServiceImpl(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @Override
    public DashboardStatsResponse getStats() {
        DashboardStatsResponse response = new DashboardStatsResponse();
        long totalApplications = jobApplicationRepository.count();
        response.setTotalApplications(totalApplications);

        Map<ApplicationStatus, Long> statusCounts = new EnumMap<>(ApplicationStatus.class);
        Arrays.stream(ApplicationStatus.values()).forEach(status -> statusCounts.put(status, 0L));
        for (StatusCountProjection projection : jobApplicationRepository.countApplicationsPerStatus()) {
            statusCounts.put(projection.getStatus(), projection.getCount());
        }
        response.setApplicationsPerStatus(statusCounts);

        long appliedCount = statusCounts.getOrDefault(ApplicationStatus.APPLIED, 0L);
        long offerCount = statusCounts.getOrDefault(ApplicationStatus.OFFER, 0L);
        double successRate = appliedCount == 0 ? 0.0 : (double) offerCount / appliedCount;
        response.setSuccessRate(successRate);

        LocalDate sevenDaysAgo = LocalDate.now().minusDays(6);
        response.setWeeklyActivity(jobApplicationRepository.countWeeklyActivity(sevenDaysAgo).stream()
                .map(activity -> new DashboardStatsResponse.WeeklyActivityResponse(
                        activity.getDate(), activity.getCount()))
                .toList());
        return response;
    }
}
