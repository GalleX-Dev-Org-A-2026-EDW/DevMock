package com.devmock.backend.controller;

import com.devmock.backend.dto.CreateUserPerformanceRequest;
import com.devmock.backend.dto.UpdateUserPerformanceRequest;
import com.devmock.backend.dto.UserPerformanceResponse;
import com.devmock.backend.service.UserPerformanceService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-performances")
public class UserPerformanceController {

    private final UserPerformanceService service;

    public UserPerformanceController(UserPerformanceService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserPerformanceResponse create(
            @Valid @RequestBody CreateUserPerformanceRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<UserPerformanceResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public UserPerformanceResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public UserPerformanceResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserPerformanceRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}