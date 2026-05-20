package com.devmock.backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class QuestionCriterionResponse {

    private UUID id;
    private BigDecimal weight;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
