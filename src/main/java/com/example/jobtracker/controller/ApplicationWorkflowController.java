package com.example.jobtracker.controller;

import com.example.jobtracker.dto.ApplicationAutomationLogResponse;
import com.example.jobtracker.dto.JobApplicationRequest;
import com.example.jobtracker.dto.JobApplicationResponse;
import com.example.jobtracker.dto.JobApplicationTimelineResponse;
import com.example.jobtracker.entity.ApplicationStatus;
import com.example.jobtracker.service.JobApplicationService;
import com.example.jobtracker.service.JobApplicationWorkflowService;
import java.util.List;
import java.net.URI;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
public class ApplicationWorkflowController {

    private final JobApplicationService jobApplicationService;
    private final JobApplicationWorkflowService workflowService;

    public ApplicationWorkflowController(JobApplicationService jobApplicationService,
            JobApplicationWorkflowService workflowService) {
        this.jobApplicationService = jobApplicationService;
        this.workflowService = workflowService;
    }

    @GetMapping
    public ResponseEntity<Page<JobApplicationResponse>> searchApplications(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "appliedDate") Pageable pageable) {
        return ResponseEntity.ok(jobApplicationService.search(status, company, fromDate, toDate, keyword, pageable));
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> create(@Valid @RequestBody JobApplicationRequest request) {
        JobApplicationResponse response = jobApplicationService.create(request);
        return ResponseEntity.created(URI.create("/applications/" + response.getId())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> update(@PathVariable Long id,
            @Valid @RequestBody JobApplicationRequest request) {
        return ResponseEntity.ok(jobApplicationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<JobApplicationTimelineResponse>> getTimeline(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.getTimeline(id));
    }

    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<String> exportCsv(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=job-applications.csv")
                .body(jobApplicationService.exportCsv(status, company, fromDate, toDate, keyword));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<JobApplicationResponse>> getPendingApplications() {
        return ResponseEntity.ok(workflowService.getPendingApplications());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<JobApplicationResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.approve(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<JobApplicationResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.reject(id));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<ApplicationAutomationLogResponse>> getLogs(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getLogs(id));
    }
}
