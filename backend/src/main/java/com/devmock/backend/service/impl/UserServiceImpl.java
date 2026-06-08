package com.devmock.backend.service.impl;

import com.devmock.backend.dto.CreateUserRequest;
import com.devmock.backend.dto.UpdateUserRequest;
import com.devmock.backend.dto.UserResponse;
import com.devmock.backend.entity.DifficultyLevel;
import com.devmock.backend.entity.User;
import com.devmock.backend.exception.EmailAlreadyExistsException;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.DifficultyLevelRepository;
import com.devmock.backend.repository.UserRepository;
import com.devmock.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final DifficultyLevelRepository difficultyLevelRepository;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder,
            DifficultyLevelRepository difficultyLevelRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.difficultyLevelRepository = difficultyLevelRepository;
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        if (repository.existsByEmailAndDeletedAtIsNull(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email " + request.getEmail() + " is already registered");
        }
        User user = repository.findByEmail(request.getEmail())
                .filter(u -> u.getDeletedAt() != null)
                .orElse(null);
        if (user != null) {
            user.setDeletedAt(null);
            user.setIsActive(true);
            user.setIsVerified(false);
            user.setEmailVerifiedAt(null);
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setRole(request.getRole());
            user.setAvatarUrl(request.getAvatarUrl());
            user.setProfessionalExperienceYears(request.getProfessionalExperienceYears());
            if (request.getCurrentLevelId() != null) {
                DifficultyLevel level = difficultyLevelRepository.findById(request.getCurrentLevelId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "DifficultyLevel " + request.getCurrentLevelId() + " not found"));
                user.setCurrentLevel(level);
            } else {
                user.setCurrentLevel(null);
            }
        } else {
            user = new User();
            user.setEmail(request.getEmail());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setRole(request.getRole());
            user.setAvatarUrl(request.getAvatarUrl());
            user.setProfessionalExperienceYears(request.getProfessionalExperienceYears());
            if (request.getCurrentLevelId() != null) {
                DifficultyLevel level = difficultyLevelRepository.findById(request.getCurrentLevelId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "DifficultyLevel " + request.getCurrentLevelId() + " not found"));
                user.setCurrentLevel(level);
            }
            user.setIsActive(true);
            user.setIsVerified(false);
        }
        return toResponse(repository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> list() {
        return repository.findAll().stream()
                .filter(u -> u.getDeletedAt() == null)
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        User user = repository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        User user = repository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new ResourceNotFoundException("User " + email + " not found"));
        return toResponse(user);
    }

    @Override
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = repository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        if (request.getFullName() != null)
            user.setFullName(request.getFullName());
        if (request.getAvatarUrl() != null)
            user.setAvatarUrl(request.getAvatarUrl());
        if (request.getRole() != null)
            user.setRole(request.getRole());
        if (request.getProfessionalExperienceYears() != null)
            user.setProfessionalExperienceYears(request.getProfessionalExperienceYears());
        if (request.getIsActive() != null)
            user.setIsActive(request.getIsActive());
        if (request.getIsVerified() != null)
            user.setIsVerified(request.getIsVerified());
        if (request.getPassword() != null)
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        if (request.getCurrentLevelId() != null) {
            DifficultyLevel level = difficultyLevelRepository.findById(request.getCurrentLevelId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DifficultyLevel " + request.getCurrentLevelId() + " not found"));
            user.setCurrentLevel(level);
        }
        return toResponse(repository.save(user));
    }

    @Override
    public void delete(UUID id) {
        User user = repository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        user.setDeletedAt(Instant.now());
        user.setIsActive(false);
        repository.save(user);
    }

    private UserResponse toResponse(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setFullName(user.getFullName());
        res.setAvatarUrl(user.getAvatarUrl());
        res.setRole(user.getRole());
        res.setProfessionalExperienceYears(user.getProfessionalExperienceYears());
        res.setIsActive(user.getIsActive());
        res.setIsVerified(user.getIsVerified());
        res.setEmailVerifiedAt(user.getEmailVerifiedAt());
        res.setLastLoginAt(user.getLastLoginAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        if (user.getCurrentLevel() != null) {
            res.setCurrentLevelId(user.getCurrentLevel().getId());
            res.setCurrentLevelName(user.getCurrentLevel().getName());
        }
        return res;
    }
}