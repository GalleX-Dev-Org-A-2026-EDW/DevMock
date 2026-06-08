package com.devmock.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devmock.backend.dto.CodeEvaluationRequest;
import com.devmock.backend.dto.CodeEvaluationResult;
import com.devmock.backend.service.CodeEvaluationService;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final CodeEvaluationService codeEvaluationService;

    public EvaluationController(CodeEvaluationService codeEvaluationService) {
        this.codeEvaluationService = codeEvaluationService;
    }

    @PostMapping("/code")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CodeEvaluationResult> evaluateCode(@RequestBody CodeEvaluationRequest request) {
        CodeEvaluationResult result = codeEvaluationService.evaluate(request);
        return ResponseEntity.ok(result);
    }
}
