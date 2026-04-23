package com.example.jobtracker.service.impl;

import com.example.jobtracker.dto.JobApplicationRequest;
import com.example.jobtracker.dto.JobApplicationResponse;
import com.example.jobtracker.entity.ApplicationStatus;
import com.example.jobtracker.entity.JobApplication;
import com.example.jobtracker.exception.ResourceNotFoundException;
import com.example.jobtracker.repository.JobApplicationRepository;
import com.example.jobtracker.service.JobApplicationService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final ModelMapper modelMapper;

    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository, ModelMapper modelMapper) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public JobApplicationResponse create(JobApplicationRequest request) {
        JobApplication application = modelMapper.map(request, JobApplication.class);
        application.setId(null);
        return modelMapper.map(jobApplicationRepository.save(application), JobApplicationResponse.class);
    }

    @Override
    public JobApplicationResponse update(Long id, JobApplicationRequest request) {
        JobApplication application = getApplication(id);
        application.setCompanyName(request.getCompanyName());
        application.setRole(request.getRole());
        application.setStatus(request.getStatus());
        application.setAppliedDate(request.getAppliedDate());
        application.setSource(request.getSource());
        application.setResumeVersion(request.getResumeVersion());
        application.setNotes(request.getNotes());
        return modelMapper.map(jobApplicationRepository.save(application), JobApplicationResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public JobApplicationResponse getById(Long id) {
        return modelMapper.map(getApplication(id), JobApplicationResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobApplicationResponse> getAll(ApplicationStatus status) {
        List<JobApplication> applications = status == null
                ? jobApplicationRepository.findAll()
                : jobApplicationRepository.findByStatus(status);
        return applications.stream()
                .map(application -> modelMapper.map(application, JobApplicationResponse.class))
                .toList();
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
}
