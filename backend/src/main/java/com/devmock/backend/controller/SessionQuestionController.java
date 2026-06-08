package com.devmock.backend.controller;

import com.devmock.backend.dto.CreateSessionQuestionRequest;
import com.devmock.backend.dto.UpdateSessionQuestionRequest;
import com.devmock.backend.dto.SessionQuestionResponse;
import com.devmock.backend.service.SessionQuestionService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/session-questions")
@PreAuthorize("isAuthenticated()")
public class SessionQuestionController {

    private final SessionQuestionService service;

    public SessionQuestionController(SessionQuestionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionQuestionResponse create(
            @Valid @RequestBody CreateSessionQuestionRequest request) {

        return service.create(request);
    }

    @GetMapping
    public List<SessionQuestionResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public SessionQuestionResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public SessionQuestionResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSessionQuestionRequest request) {

        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}