package com.example.jobtracker.service.impl;

import com.example.jobtracker.dto.ResumeOptimizeRequest;
import com.example.jobtracker.dto.ResumeOptimizerClientResponse;
import com.example.jobtracker.dto.ResumeVersionResponse;
import com.example.jobtracker.entity.CvDocument;
import com.example.jobtracker.entity.ResumeVersion;
import com.example.jobtracker.exception.ExternalServiceException;
import com.example.jobtracker.exception.ResourceNotFoundException;
import com.example.jobtracker.repository.CvDocumentRepository;
import com.example.jobtracker.repository.ResumeVersionRepository;
import com.example.jobtracker.service.ResumeProcessingClient;
import com.example.jobtracker.service.ResumeService;
import java.time.OffsetDateTime;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class ResumeServiceImpl implements ResumeService {

    private final CvDocumentRepository cvDocumentRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final ResumeProcessingClient resumeProcessingClient;
    private final ModelMapper modelMapper;

    public ResumeServiceImpl(CvDocumentRepository cvDocumentRepository,
            ResumeVersionRepository resumeVersionRepository,
            ResumeProcessingClient resumeProcessingClient,
            ModelMapper modelMapper) {
        this.cvDocumentRepository = cvDocumentRepository;
        this.resumeVersionRepository = resumeVersionRepository;
        this.resumeProcessingClient = resumeProcessingClient;
        this.modelMapper = modelMapper;
    }

    @Override
    public ResumeVersionResponse optimize(ResumeOptimizeRequest request) {
        CvDocument document = getDocument(request.getCvDocumentId());
        ResumeVersion latestVersion = resumeVersionRepository.findTopByCvDocumentIdOrderByVersionNumberDesc(
                        document.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No resume version found for CV document id: " + document.getId()));
        ResumeVersion originalVersion = resumeVersionRepository.findByCvDocumentIdAndVersionNumber(document.getId(), 1)
                .orElse(latestVersion);

        ResumeOptimizerClientResponse optimized = resumeProcessingClient.optimize(
                request.getJobDescription(),
                originalVersion.getContent());

        if (optimized == null || !StringUtils.hasText(optimized.getTailoredResume())) {
            throw new ExternalServiceException("Resume optimizer service returned an empty response");
        }

        ResumeVersion version = new ResumeVersion();
        version.setCvDocument(document);
        version.setVersionNumber(latestVersion.getVersionNumber() + 1);
        version.setVersionLabel(StringUtils.hasText(request.getVersionLabel())
                ? request.getVersionLabel()
                : "Tailored v" + (latestVersion.getVersionNumber() + 1));
        version.setJobDescription(request.getJobDescription());
        version.setContent(optimized.getTailoredResume());
        version.setCreatedAt(OffsetDateTime.now());

        return toResponse(resumeVersionRepository.save(version));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeVersionResponse> getVersions(Long cvDocumentId) {
        if (!cvDocumentRepository.existsById(cvDocumentId)) {
            throw new ResourceNotFoundException("CV document not found with id: " + cvDocumentId);
        }
        return resumeVersionRepository.findByCvDocumentIdOrderByVersionNumberDesc(cvDocumentId).stream()
                .map(this::toResponse)
                .toList();
    }

    private CvDocument getDocument(Long id) {
        return cvDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CV document not found with id: " + id));
    }

    private ResumeVersionResponse toResponse(ResumeVersion version) {
        ResumeVersionResponse response = modelMapper.map(version, ResumeVersionResponse.class);
        response.setCvDocumentId(version.getCvDocument().getId());
        return response;
    }
}
