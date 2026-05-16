package com.devmock.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.devmock.backend.entity.en_enum.RankingPeriod;

import jakarta.validation.constraints.NotNull;

public class CreateRankingRequest {

    @NotNull(message = "period is required")
    private RankingPeriod period;

    private LocalDate periodStartDate;

    private LocalDate periodEndDate;

    private BigDecimal totalScore;

    private Integer totalSessions;

    private Integer rankPosition;

    private Instant calculatedAt;

    // Getters y Setters

    public RankingPeriod getPeriod() {
        return period;
    }

    public void setPeriod(RankingPeriod period) {
        this.period = period;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(LocalDate periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Integer getRankPosition() {
        return rankPosition;
    }

    public void setRankPosition(Integer rankPosition) {
        this.rankPosition = rankPosition;
    }

    public Instant getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(Instant calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}