package com.example.jobtracker.service.impl;

import com.example.jobtracker.dto.JobScraperClientRequest;
import com.example.jobtracker.dto.JobScraperClientResponse;
import com.example.jobtracker.exception.ExternalServiceException;
import com.example.jobtracker.service.JobScraperClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class FastApiJobScraperClient implements JobScraperClient {

    private final RestClient jobScraperRestClient;

    public FastApiJobScraperClient(@Qualifier("jobScraperRestClient") RestClient jobScraperRestClient) {
        this.jobScraperRestClient = jobScraperRestClient;
    }

    @Override
    public JobScraperClientResponse scrape(JobScraperClientRequest request) {
        try {
            return jobScraperRestClient.post()
                    .uri("/scrape")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(JobScraperClientResponse.class);
        } catch (ResourceAccessException exception) {
            throw new ExternalServiceException("Job scraper service timed out or is unavailable", exception);
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Job scraper service failed", exception);
        }
    }
}
