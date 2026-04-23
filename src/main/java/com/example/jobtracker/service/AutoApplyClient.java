package com.example.jobtracker.service;

import com.example.jobtracker.dto.AutoApplyClientRequest;
import com.example.jobtracker.dto.AutoApplyClientResponse;

public interface AutoApplyClient {

    AutoApplyClientResponse apply(AutoApplyClientRequest request);
}
