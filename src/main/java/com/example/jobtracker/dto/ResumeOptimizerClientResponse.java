package com.example.jobtracker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResumeOptimizerClientResponse {

    @JsonProperty("tailored_resume")
    private String tailoredResume;

    public String getTailoredResume() {
        return tailoredResume;
    }

    public void setTailoredResume(String tailoredResume) {
        this.tailoredResume = tailoredResume;
    }
}
