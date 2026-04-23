package com.example.jobtracker.controller;

import com.example.jobtracker.dto.JobPreferenceRequest;
import com.example.jobtracker.dto.JobPreferenceResponse;
import com.example.jobtracker.service.JobPreferenceService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/job-preferences")
public class JobPreferenceController {

    private final JobPreferenceService jobPreferenceService;

    public JobPreferenceController(JobPreferenceService jobPreferenceService) {
        this.jobPreferenceService = jobPreferenceService;
    }

    @PostMapping
    public ResponseEntity<JobPreferenceResponse> create(@Valid @RequestBody JobPreferenceRequest request) {
        JobPreferenceResponse response = jobPreferenceService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/job-preferences/" + response.getId())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPreferenceResponse> update(@PathVariable Long id,
            @Valid @RequestBody JobPreferenceRequest request) {
        return ResponseEntity.ok(jobPreferenceService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPreferenceResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobPreferenceService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<JobPreferenceResponse>> getByUserId(@RequestParam Long userId) {
        return ResponseEntity.ok(jobPreferenceService.getByUserId(userId));
    }
}
