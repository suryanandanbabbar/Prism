package com.example.jobtracker.service.impl;

import com.example.jobtracker.dto.ParsedResumeResponse;
import com.example.jobtracker.dto.ResumeOptimizerClientRequest;
import com.example.jobtracker.dto.ResumeOptimizerClientResponse;
import com.example.jobtracker.exception.ExternalServiceException;
import com.example.jobtracker.service.ResumeProcessingClient;
import java.nio.file.Path;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class FastApiResumeProcessingClient implements ResumeProcessingClient {

    private final RestClient resumeProcessingRestClient;

    public FastApiResumeProcessingClient(RestClient resumeProcessingRestClient) {
        this.resumeProcessingRestClient = resumeProcessingRestClient;
    }

    @Override
    public ParsedResumeResponse parse(Path resumePath) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(resumePath));

        try {
            return resumeProcessingRestClient.post()
                    .uri("/parse")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(ParsedResumeResponse.class);
        } catch (ResourceAccessException exception) {
            throw new ExternalServiceException("Resume parser service timed out or is unavailable", exception);
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Resume parser service failed", exception);
        }
    }

    @Override
    public ResumeOptimizerClientResponse optimize(String jobDescription, String originalResume) {
        ResumeOptimizerClientRequest request = new ResumeOptimizerClientRequest(jobDescription, originalResume);
        try {
            return resumeProcessingRestClient.post()
                    .uri("/optimize")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ResumeOptimizerClientResponse.class);
        } catch (ResourceAccessException exception) {
            throw new ExternalServiceException("Resume optimizer service timed out or is unavailable", exception);
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Resume optimizer service failed", exception);
        }
    }
}
