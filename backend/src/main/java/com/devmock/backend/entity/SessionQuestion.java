package com.devmock.backend.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "session_question")
public class SessionQuestion {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
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

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @ManyToOne
    private InterviewSession session;

    //@ManyToOne
    //private Question question;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Getters y Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public InterviewSession getSession() {
        return session;
    }

    public void setSession(InterviewSession session) {
        this.session = session;
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}