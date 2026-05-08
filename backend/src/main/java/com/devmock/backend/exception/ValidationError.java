package com.devmock.backend.exception;

import java.time.Instant;
import java.util.Map;

public class ValidationError extends ApiError {
    private Map<String, String> fields;

    public ValidationError(String code, String message, Instant timestamp, String path, Map<String, String> fields) {
        super(code, message, timestamp, path);
        this.fields = fields;
    }

    public Map<String, String> getFields() {
        return fields;
    }
}