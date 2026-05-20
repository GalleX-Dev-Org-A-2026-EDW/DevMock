package com.devmock.backend.dto;

import java.math.BigDecimal;

public class UpdateQuestionCriterionRequest {

    private BigDecimal weight;

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
