package com.example.jobtracker.controller;

import com.example.jobtracker.dto.CvDocumentResponse;
import com.example.jobtracker.service.CvDocumentService;
import java.net.URI;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/cvs")
public class CvDocumentController {

    private final CvDocumentService cvDocumentService;

    public CvDocumentController(CvDocumentService cvDocumentService) {
        this.cvDocumentService = cvDocumentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CvDocumentResponse> upload(@RequestParam Long userId, @RequestParam MultipartFile file) {
        CvDocumentResponse response = cvDocumentService.upload(userId, file);
        return ResponseEntity.created(URI.create("/api/v1/cvs/" + response.getId())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CvDocumentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cvDocumentService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<CvDocumentResponse>> getByUserId(@RequestParam Long userId) {
        return ResponseEntity.ok(cvDocumentService.getByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cvDocumentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
