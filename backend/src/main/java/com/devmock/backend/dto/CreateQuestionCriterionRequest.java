package com.devmock.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class CreateQuestionCriterionRequest {

    @NotNull(message = "weight is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "weight must be zero or positive")
    @DecimalMax(value = "100.0", inclusive = true, message = "weight must be at most 100")
    private BigDecimal weight;

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
