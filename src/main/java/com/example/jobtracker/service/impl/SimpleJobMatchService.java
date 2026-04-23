package com.example.jobtracker.service.impl;

import com.example.jobtracker.entity.JobApplication;
import com.example.jobtracker.entity.JobPreference;
import com.example.jobtracker.service.JobMatchService;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SimpleJobMatchService implements JobMatchService {

    @Override
    public double score(JobPreference preference, JobApplication application) {
        List<String> skills = parseSkills(preference.getSkills());
        if (skills.isEmpty()) {
            return 0.0;
        }

        String searchableText = normalize(application.getRole() + " " + application.getJobDescription());
        long matchedSkills = skills.stream()
                .map(this::normalize)
                .filter(searchableText::contains)
                .count();
        return Math.min(1.0, (double) matchedSkills / skills.size());
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

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}
