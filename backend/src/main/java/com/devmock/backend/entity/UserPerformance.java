package com.devmock.backend.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "user_performance")
public class UserPerformance {

    @Id
    @GeneratedValue
    private UUID id;

    private Integer totalQuestionsAnswered;

    private Integer totalCorrect;

    private BigDecimal accuracyPercentage;

    private BigDecimal avgTimeSeconds;

    private BigDecimal avgScore;

    private String strengths;

    private String weaknesses;

    private Instant lastPracticedAt;

    @Column(nullable = false)
    private Instant updatedAt;

    // Relaciones después

    @ManyToOne
    private User user;

    //@ManyToOne
    //private Category category;

    //@ManyToOne
    //private DifficultyLevel difficulty;

    @PrePersist
    void onCreate() {
        this.updatedAt = Instant.now();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}