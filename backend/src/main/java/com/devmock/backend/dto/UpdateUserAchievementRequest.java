package com.devmock.backend.dto;

import java.time.Instant;

public class UpdateUserAchievementRequest {

    private Instant unlockedAt;

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