package com.example.jobtracker.service;

import com.example.jobtracker.dto.ResumeOptimizeRequest;
import com.example.jobtracker.dto.ResumeVersionResponse;
import java.util.List;

public interface ResumeService {

    ResumeVersionResponse optimize(ResumeOptimizeRequest request);

    List<ResumeVersionResponse> getVersions(Long cvDocumentId);
}
