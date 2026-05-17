package com.devmock.backend.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public class CreateUserAchievementRequest {

    @NotNull(message = "unlockedAt is required")
    private Instant unlockedAt;

    @NotNull(message = "isViewed is required")
    private Boolean isViewed;

    // Getters y Setters

    public Instant getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(Instant unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public Boolean getIsViewed() {
        return isViewed;
    }

    public void setIsViewed(Boolean isViewed) {
        this.isViewed = isViewed;
    }
}