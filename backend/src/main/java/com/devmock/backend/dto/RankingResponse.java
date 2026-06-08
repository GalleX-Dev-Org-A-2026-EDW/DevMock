package com.devmock.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.devmock.backend.entity.en_enum.RankingPeriod;
import com.devmock.backend.entity.en_enum.UserRole;

public class RankingResponse {

    private UUID id;
    private RankingPeriod period;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private BigDecimal totalScore;
    private Integer totalSessions;
    private Integer rankPosition;
    private Instant calculatedAt;
    private UUID userId;
    private UUID categoryId;
    private UUID difficultyId;
    private String userName;
    private UserRole userRole;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
