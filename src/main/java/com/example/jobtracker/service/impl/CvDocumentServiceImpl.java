package com.example.jobtracker.service.impl;

import com.example.jobtracker.config.FileStorageProperties;
import com.example.jobtracker.dto.CvDocumentResponse;
import com.example.jobtracker.entity.CvDocument;
import com.example.jobtracker.entity.User;
import com.example.jobtracker.exception.FileStorageException;
import com.example.jobtracker.exception.ResourceNotFoundException;
import com.example.jobtracker.repository.CvDocumentRepository;
import com.example.jobtracker.repository.UserRepository;
import com.example.jobtracker.service.CvDocumentService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class CvDocumentServiceImpl implements CvDocumentService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    private final CvDocumentRepository cvDocumentRepository;
    private final UserRepository userRepository;
    private final FileStorageProperties fileStorageProperties;
    private final ModelMapper modelMapper;

    public CvDocumentServiceImpl(CvDocumentRepository cvDocumentRepository, UserRepository userRepository,
            FileStorageProperties fileStorageProperties, ModelMapper modelMapper) {
        this.cvDocumentRepository = cvDocumentRepository;
        this.userRepository = userRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.modelMapper = modelMapper;
    }

    @Override
    public CvDocumentResponse upload(Long userId, MultipartFile file) {
        validateFile(file);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + extension;
        Path uploadRoot = Path.of(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        Path destination = uploadRoot.resolve(storedFileName).normalize();

        if (!destination.startsWith(uploadRoot)) {
            throw new FileStorageException("Invalid CV file path");
        }

        try {
            Files.createDirectories(uploadRoot);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new FileStorageException("Could not store CV file", exception);
        }

        CvDocument document = new CvDocument();
        document.setOriginalFileName(originalFileName);
        document.setStoredFileName(storedFileName);
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setFilePath(destination.toString());
        document.setUploadedAt(OffsetDateTime.now());
        document.setUser(user);

        return toResponse(cvDocumentRepository.save(document));
    }

    @Override
    @Transactional(readOnly = true)
    public CvDocumentResponse getById(Long id) {
        return toResponse(getDocument(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CvDocumentResponse> getByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return cvDocumentRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        CvDocument document = getDocument(id);
        try {
            Files.deleteIfExists(Path.of(document.getFilePath()));
        } catch (IOException exception) {
            throw new FileStorageException("Could not delete CV file", exception);
        }
        cvDocumentRepository.delete(document);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("CV file is required");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new FileStorageException("Only PDF, DOC, and DOCX CV files are supported");
        }
        if (!StringUtils.hasText(file.getOriginalFilename())) {
            throw new FileStorageException("CV file name is required");
        }
    }

    private String getExtension(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');
        return extensionIndex >= 0 ? fileName.substring(extensionIndex) : "";
    }

    private CvDocument getDocument(Long id) {
        return cvDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CV document not found with id: " + id));
    }

    private CvDocumentResponse toResponse(CvDocument document) {
        CvDocumentResponse response = modelMapper.map(document, CvDocumentResponse.class);
        response.setUserId(document.getUser().getId());
        return response;
    }
}
