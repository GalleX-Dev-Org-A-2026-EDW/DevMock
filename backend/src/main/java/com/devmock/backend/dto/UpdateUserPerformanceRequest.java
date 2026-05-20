package com.devmock.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class UpdateUserPerformanceRequest {

    private Integer totalQuestionsAnswered;

    private Integer totalCorrect;

    private BigDecimal accuracyPercentage;

    private BigDecimal avgTimeSeconds;

    private BigDecimal avgScore;

    private String strengths;

    private String weaknesses;

    private Instant lastPracticedAt;

    // Getters y Setters

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
}