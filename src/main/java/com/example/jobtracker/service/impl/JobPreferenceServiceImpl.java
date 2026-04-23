package com.example.jobtracker.service.impl;

import com.example.jobtracker.dto.JobPreferenceRequest;
import com.example.jobtracker.dto.JobPreferenceResponse;
import com.example.jobtracker.entity.JobPreference;
import com.example.jobtracker.entity.User;
import com.example.jobtracker.exception.ResourceNotFoundException;
import com.example.jobtracker.repository.JobPreferenceRepository;
import com.example.jobtracker.repository.UserRepository;
import com.example.jobtracker.service.JobPreferenceService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobPreferenceServiceImpl implements JobPreferenceService {

    private final JobPreferenceRepository jobPreferenceRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public JobPreferenceServiceImpl(JobPreferenceRepository jobPreferenceRepository, UserRepository userRepository,
            ModelMapper modelMapper) {
        this.jobPreferenceRepository = jobPreferenceRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public JobPreferenceResponse create(JobPreferenceRequest request) {
        User user = getUser(request.getUserId());
        JobPreference preference = modelMapper.map(request, JobPreference.class);
        preference.setId(null);
        preference.setUser(user);
        return toResponse(jobPreferenceRepository.save(preference));
    }

    @Override
    public JobPreferenceResponse update(Long id, JobPreferenceRequest request) {
        JobPreference preference = getPreference(id);
        User user = getUser(request.getUserId());
        preference.setRole(request.getRole());
        preference.setLocation(request.getLocation());
        preference.setSkills(request.getSkills());
        preference.setSalaryRange(request.getSalaryRange());
        preference.setUser(user);
        return toResponse(jobPreferenceRepository.save(preference));
    }

    @Override
    @Transactional(readOnly = true)
    public JobPreferenceResponse getById(Long id) {
        return toResponse(getPreference(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobPreferenceResponse> getByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return jobPreferenceRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private JobPreference getPreference(Long id) {
        return jobPreferenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job preference not found with id: " + id));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private JobPreferenceResponse toResponse(JobPreference preference) {
        JobPreferenceResponse response = modelMapper.map(preference, JobPreferenceResponse.class);
        response.setUserId(preference.getUser().getId());
        return response;
    }
}
