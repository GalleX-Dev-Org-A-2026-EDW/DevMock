package com.devmock.backend.service;

import com.devmock.backend.dto.CreateUserRequest;
import com.devmock.backend.dto.UpdateUserRequest;
import com.devmock.backend.dto.UserResponse;
import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(CreateUserRequest request);

    List<UserResponse> list();

    UserResponse getById(UUID id);

    UserResponse getByEmail(String email);

    UserResponse update(UUID id, UpdateUserRequest request);

    void delete(UUID id);
}
