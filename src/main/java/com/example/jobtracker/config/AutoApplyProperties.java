package com.example.jobtracker.config;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auto-apply")
public class AutoApplyProperties {

    private String baseUrl = "http://localhost:8020";
    private Duration connectTimeout = Duration.ofSeconds(3);
    private Duration readTimeout = Duration.ofSeconds(60);
    private double matchThreshold = 0.6;
    private Set<String> disabledPlatforms = new HashSet<>();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public double getMatchThreshold() {
        return matchThreshold;
    }

    public void setMatchThreshold(double matchThreshold) {
        this.matchThreshold = matchThreshold;
    }

    public Set<String> getDisabledPlatforms() {
        return disabledPlatforms;
    }

    public void setDisabledPlatforms(Set<String> disabledPlatforms) {
        this.disabledPlatforms = disabledPlatforms;
    }
}
