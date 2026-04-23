package com.example.jobtracker.dto;

import com.example.jobtracker.entity.ApplicationStatus;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class DashboardStatsResponse {

    private long totalApplications;
    private Map<ApplicationStatus, Long> applicationsPerStatus = new EnumMap<>(ApplicationStatus.class);
    private double successRate;
    private List<WeeklyActivityResponse> weeklyActivity = new ArrayList<>();

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public Map<ApplicationStatus, Long> getApplicationsPerStatus() {
        return applicationsPerStatus;
    }

    public void setApplicationsPerStatus(Map<ApplicationStatus, Long> applicationsPerStatus) {
        this.applicationsPerStatus = applicationsPerStatus;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public List<WeeklyActivityResponse> getWeeklyActivity() {
        return weeklyActivity;
    }

    public void setWeeklyActivity(List<WeeklyActivityResponse> weeklyActivity) {
        this.weeklyActivity = weeklyActivity;
    }

    public static class WeeklyActivityResponse {

        private LocalDate date;
        private long count;

        public WeeklyActivityResponse() {
        }

        public WeeklyActivityResponse(LocalDate date, long count) {
            this.date = date;
            this.count = count;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}
