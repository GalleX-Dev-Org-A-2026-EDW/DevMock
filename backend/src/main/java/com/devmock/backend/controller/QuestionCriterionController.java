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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devmock.backend.dto.CreateQuestionCriterionRequest;
import com.devmock.backend.dto.QuestionCriterionResponse;
import com.devmock.backend.dto.UpdateQuestionCriterionRequest;
import com.devmock.backend.service.QuestionCriterionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/question-criteria")
public class QuestionCriterionController {

    private final QuestionCriterionService service;

    public QuestionCriterionController(QuestionCriterionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionCriterionResponse create(@Valid @RequestBody CreateQuestionCriterionRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<QuestionCriterionResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public QuestionCriterionResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public QuestionCriterionResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateQuestionCriterionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
