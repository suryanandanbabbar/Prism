package com.example.jobtracker.controller;

import com.example.jobtracker.dto.ResumeOptimizeRequest;
import com.example.jobtracker.dto.ResumeVersionResponse;
import com.example.jobtracker.service.ResumeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/optimize")
    public ResponseEntity<ResumeVersionResponse> optimize(@Valid @RequestBody ResumeOptimizeRequest request) {
        return ResponseEntity.ok(resumeService.optimize(request));
    }

    @GetMapping("/versions")
    public ResponseEntity<List<ResumeVersionResponse>> getVersions(@RequestParam Long cvDocumentId) {
        return ResponseEntity.ok(resumeService.getVersions(cvDocumentId));
    }
}
