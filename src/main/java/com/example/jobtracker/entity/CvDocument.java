package com.example.jobtracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "cv_documents")
public class CvDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "stored_file_name", nullable = false, unique = true, length = 255)
    private String storedFileName;

    @Column(name = "content_type", length = 120)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "uploaded_at", nullable = false)
    private OffsetDateTime uploadedAt;

    @Column(name = "parsed_name", length = 180)
    private String parsedName;

    @Column(name = "parsed_skills", columnDefinition = "TEXT")
    private String parsedSkills;

    @Column(name = "parsed_experience", columnDefinition = "TEXT")
    private String parsedExperience;

    @Column(name = "parsed_projects", columnDefinition = "TEXT")
    private String parsedProjects;

    @Column(name = "parse_status", nullable = false, length = 30)
    private String parseStatus;

    @Column(name = "parse_error", columnDefinition = "TEXT")
    private String parseError;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
