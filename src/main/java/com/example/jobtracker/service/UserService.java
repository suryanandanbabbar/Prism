package com.example.jobtracker.service;

import com.example.jobtracker.dto.UserRequest;
import com.example.jobtracker.dto.UserResponse;
import java.util.List;

public interface UserService {

    UserResponse create(UserRequest request);

    UserResponse getById(Long id);

    List<UserResponse> getAll();
}
