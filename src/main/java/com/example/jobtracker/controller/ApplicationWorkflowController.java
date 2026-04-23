package com.example.jobtracker.controller;

import com.example.jobtracker.dto.ApplicationAutomationLogResponse;
import com.example.jobtracker.dto.JobApplicationResponse;
import com.example.jobtracker.service.JobApplicationWorkflowService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
public class ApplicationWorkflowController {

    private final JobApplicationWorkflowService workflowService;

    public ApplicationWorkflowController(JobApplicationWorkflowService workflowService) {
        this.workflowService = workflowService;
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
