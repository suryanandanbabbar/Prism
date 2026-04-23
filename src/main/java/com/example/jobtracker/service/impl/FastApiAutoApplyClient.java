package com.example.jobtracker.service.impl;

import com.example.jobtracker.dto.AutoApplyClientRequest;
import com.example.jobtracker.dto.AutoApplyClientResponse;
import com.example.jobtracker.exception.ExternalServiceException;
import com.example.jobtracker.service.AutoApplyClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class FastApiAutoApplyClient implements AutoApplyClient {

    private final RestClient autoApplyRestClient;

    public FastApiAutoApplyClient(@Qualifier("autoApplyRestClient") RestClient autoApplyRestClient) {
        this.autoApplyRestClient = autoApplyRestClient;
    }

    @Override
    public AutoApplyClientResponse apply(AutoApplyClientRequest request) {
        try {
            return autoApplyRestClient.post()
                    .uri("/apply")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AutoApplyClientResponse.class);
        } catch (ResourceAccessException exception) {
            throw new ExternalServiceException("Auto apply service timed out or is unavailable", exception);
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Auto apply service failed", exception);
        }
    }
}
