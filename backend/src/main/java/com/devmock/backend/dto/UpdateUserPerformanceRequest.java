package com.devmock.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class UpdateUserPerformanceRequest {

    private Integer totalQuestionsAnswered;
    private Integer totalCorrect;
    private BigDecimal accuracyPercentage;
    private BigDecimal avgTimeSeconds;
    private BigDecimal avgScore;
    private String strengths;
    private String weaknesses;
    private Instant lastPracticedAt;
    private UUID userId;
    private UUID categoryId;
    private UUID difficultyId;

    public Integer getTotalQuestionsAnswered() {
        return totalQuestionsAnswered;
    }

    public void setTotalQuestionsAnswered(Integer totalQuestionsAnswered) {
        this.totalQuestionsAnswered = totalQuestionsAnswered;
    }

    public Integer getTotalCorrect() {
        return totalCorrect;
    }

    public void setTotalCorrect(Integer totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public BigDecimal getAccuracyPercentage() {
        return accuracyPercentage;
    }

    public void setAccuracyPercentage(BigDecimal accuracyPercentage) {
        this.accuracyPercentage = accuracyPercentage;
    }

    public BigDecimal getAvgTimeSeconds() {
        return avgTimeSeconds;
    }

    public void setAvgTimeSeconds(BigDecimal avgTimeSeconds) {
        this.avgTimeSeconds = avgTimeSeconds;
    }

    public BigDecimal getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(BigDecimal avgScore) {
        this.avgScore = avgScore;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public Instant getLastPracticedAt() {
        return lastPracticedAt;
    }

    public void setLastPracticedAt(Instant lastPracticedAt) {
        this.lastPracticedAt = lastPracticedAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public UUID getDifficultyId() {
        return difficultyId;
    }

    public void setDifficultyId(UUID difficultyId) {
        this.difficultyId = difficultyId;
    }
}
