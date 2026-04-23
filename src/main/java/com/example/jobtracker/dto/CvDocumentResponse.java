package com.example.jobtracker.dto;

import java.time.OffsetDateTime;

public class CvDocumentResponse {

    private Long id;
    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long fileSize;
    private String filePath;
    private OffsetDateTime uploadedAt;
    private Long userId;
    private String parsedName;
    private String parsedSkills;
    private String parsedExperience;
    private String parsedProjects;
    private String parseStatus;
    private String parseError;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public OffsetDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(OffsetDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getParsedName() {
        return parsedName;
    }

    public void setParsedName(String parsedName) {
        this.parsedName = parsedName;
    }

    public String getParsedSkills() {
        return parsedSkills;
    }

    public void setParsedSkills(String parsedSkills) {
        this.parsedSkills = parsedSkills;
    }

    public String getParsedExperience() {
        return parsedExperience;
    }

    public void setParsedExperience(String parsedExperience) {
        this.parsedExperience = parsedExperience;
    }

    public String getParsedProjects() {
        return parsedProjects;
    }

    public void setParsedProjects(String parsedProjects) {
        this.parsedProjects = parsedProjects;
    }

    public String getParseStatus() {
        return parseStatus;
    }

    public void setParseStatus(String parseStatus) {
        this.parseStatus = parseStatus;
    }

    public String getParseError() {
        return parseError;
    }

    public void setParseError(String parseError) {
        this.parseError = parseError;
    }
}
