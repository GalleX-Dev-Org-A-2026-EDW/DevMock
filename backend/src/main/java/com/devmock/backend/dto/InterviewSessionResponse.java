package com.devmock.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.devmock.backend.entity.en_enum.SessionStatus;

public class InterviewSessionResponse {

    private UUID id;
    private SessionStatus status;
    private Instant startedAt;
    private Instant finishedAt;
    private Integer totalTimeUsedSeconds;
    private BigDecimal finalScore;
    private BigDecimal correctnessScore;
    private BigDecimal efficiencyScore;
    private BigDecimal logicScore;
    private BigDecimal clarityScore;
    private UUID userId;
    private UUID interviewTypeId;
    private UUID difficultyId;
    private UUID categoryId;
    private Instant createdAt;
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Integer getTotalTimeUsedSeconds() {
        return totalTimeUsedSeconds;
    }

    public void setTotalTimeUsedSeconds(Integer totalTimeUsedSeconds) {
        this.totalTimeUsedSeconds = totalTimeUsedSeconds;
    }

    public BigDecimal getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(BigDecimal finalScore) {
        this.finalScore = finalScore;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getInterviewTypeId() {
        return interviewTypeId;
    }

    public void setInterviewTypeId(UUID interviewTypeId) {
        this.interviewTypeId = interviewTypeId;
    }

    public UUID getDifficultyId() {
        return difficultyId;
    }

    public void setDifficultyId(UUID difficultyId) {
        this.difficultyId = difficultyId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
