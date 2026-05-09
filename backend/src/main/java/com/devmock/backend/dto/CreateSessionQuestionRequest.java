package com.devmock.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public class CreateSessionQuestionRequest {

    @NotNull(message = "questionOrder is required")
    private Integer questionOrder;

    private Integer assignedTimeSeconds;
    private Integer timeUsedSeconds;

    private String userAnswer;

    private BigDecimal obtainedPoints;
    private BigDecimal correctnessScore;
    private BigDecimal efficiencyScore;
    private BigDecimal logicScore;
    private BigDecimal clarityScore;

    private String evaluationFeedback;

    private Instant answeredAt;

    // Getters y Setters

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Integer getAssignedTimeSeconds() {
        return assignedTimeSeconds;
    }

    public void setAssignedTimeSeconds(Integer assignedTimeSeconds) {
        this.assignedTimeSeconds = assignedTimeSeconds;
    }

    public Integer getTimeUsedSeconds() {
        return timeUsedSeconds;
    }

    public void setTimeUsedSeconds(Integer timeUsedSeconds) {
        this.timeUsedSeconds = timeUsedSeconds;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public BigDecimal getObtainedPoints() {
        return obtainedPoints;
    }

    public void setObtainedPoints(BigDecimal obtainedPoints) {
        this.obtainedPoints = obtainedPoints;
    }

    public BigDecimal getCorrectnessScore() {
        return correctnessScore;
    }

    public void setCorrectnessScore(BigDecimal correctnessScore) {
        this.correctnessScore = correctnessScore;
    }

    public BigDecimal getEfficiencyScore() {
        return efficiencyScore;
    }

    public void setEfficiencyScore(BigDecimal efficiencyScore) {
        this.efficiencyScore = efficiencyScore;
    }

    public BigDecimal getLogicScore() {
        return logicScore;
    }

    public void setLogicScore(BigDecimal logicScore) {
        this.logicScore = logicScore;
    }

    public BigDecimal getClarityScore() {
        return clarityScore;
    }

    public void setClarityScore(BigDecimal clarityScore) {
        this.clarityScore = clarityScore;
    }

    public String getEvaluationFeedback() {
        return evaluationFeedback;
    }

    public void setEvaluationFeedback(String evaluationFeedback) {
        this.evaluationFeedback = evaluationFeedback;
    }

    public Instant getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(Instant answeredAt) {
        this.answeredAt = answeredAt;
    }
}