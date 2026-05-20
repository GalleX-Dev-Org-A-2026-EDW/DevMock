package com.devmock.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devmock.backend.dto.CreateInterviewTypeRequest;
import com.devmock.backend.dto.InterviewTypeResponse;
import com.devmock.backend.dto.UpdateInterviewTypeRequest;
import com.devmock.backend.service.InterviewTypeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/interview-types")
public class InterviewTypeController {

    private final InterviewTypeService service;

    public InterviewTypeController(InterviewTypeService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewTypeResponse create(@Valid @RequestBody CreateInterviewTypeRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<InterviewTypeResponse> list(
            @RequestParam(name = "activeOnly", required = false, defaultValue = "false") boolean activeOnly) {
        return activeOnly ? service.listActive() : service.list();
    }

    @GetMapping("/{id}")
    public InterviewTypeResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/by-slug/{slug}")
    public InterviewTypeResponse getBySlug(@PathVariable String slug) {
        return service.getBySlug(slug);
    }

    @PutMapping("/{id}")
    public InterviewTypeResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInterviewTypeRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
