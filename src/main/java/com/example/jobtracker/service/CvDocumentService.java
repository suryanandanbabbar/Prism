package com.example.jobtracker.service;

import com.example.jobtracker.dto.CvDocumentResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface CvDocumentService {

    CvDocumentResponse upload(Long userId, MultipartFile file);

    CvDocumentResponse getById(Long id);

    List<CvDocumentResponse> getByUserId(Long userId);

    void delete(Long id);
}
