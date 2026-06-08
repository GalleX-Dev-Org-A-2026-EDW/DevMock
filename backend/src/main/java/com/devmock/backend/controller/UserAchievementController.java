package com.devmock.backend.controller;

import com.devmock.backend.dto.CreateUserAchievementRequest;
import com.devmock.backend.dto.UpdateUserAchievementRequest;
import com.devmock.backend.dto.UserAchievementResponse;
import com.devmock.backend.service.UserAchievementService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-achievements")
public class UserAchievementController {

    private final UserAchievementService service;

    public UserAchievementController(UserAchievementService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserAchievementResponse create(
            @Valid @RequestBody CreateUserAchievementRequest request) {

        return service.create(request);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<UserAchievementResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public UserAchievementResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserAchievementResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserAchievementRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}