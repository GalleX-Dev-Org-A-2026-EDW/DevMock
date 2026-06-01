package com.devmock.backend.dto;

import java.time.Instant;
import java.util.UUID;

public class UserAchievementResponse {

    private UUID id;
    private UUID userId;
    private UUID achievementId;
    private Instant unlockedAt;
    private Boolean isViewed;
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(UUID achievementId) {
        this.achievementId = achievementId;
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
