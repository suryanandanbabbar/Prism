package com.example.jobtracker.service.impl;

import com.example.jobtracker.dto.UserRequest;
import com.example.jobtracker.dto.UserResponse;
import com.example.jobtracker.entity.User;
import com.example.jobtracker.exception.DuplicateResourceException;
import com.example.jobtracker.exception.ResourceNotFoundException;
import com.example.jobtracker.repository.UserRepository;
import com.example.jobtracker.service.UserService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User already exists with email: " + request.getEmail());
        }
        User user = modelMapper.map(request, User.class);
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return modelMapper.map(findUserById(id), UserResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .toList();
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
