package com.devmock.backend.service;

import com.devmock.backend.dto.CodeEvaluationRequest;
import com.devmock.backend.dto.CodeEvaluationResult;

public interface CodeEvaluationService {

    CodeEvaluationResult evaluate(CodeEvaluationRequest request);
}
