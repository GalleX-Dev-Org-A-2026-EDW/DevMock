package com.devmock.backend.controller;

import com.devmock.backend.dto.CreateInterviewSessionRequest;
import com.devmock.backend.dto.UpdateInterviewSessionRequest;
import com.devmock.backend.dto.InterviewSessionResponse;
import com.devmock.backend.service.InterviewSessionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interview-sessions")
public class InterviewSessionController {

    private final InterviewSessionService service;

    public InterviewSessionController(InterviewSessionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewSessionResponse create(@Valid @RequestBody CreateInterviewSessionRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<InterviewSessionResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public InterviewSessionResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public InterviewSessionResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInterviewSessionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}