package com.devmock.backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class QuestionCriterionResponse {

    private UUID id;
    private UUID questionId;
    private UUID criterionId;
    private BigDecimal weight;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public UUID getCriterionId() {
        return criterionId;
    }

    public void setCriterionId(UUID criterionId) {
        this.criterionId = criterionId;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
}
