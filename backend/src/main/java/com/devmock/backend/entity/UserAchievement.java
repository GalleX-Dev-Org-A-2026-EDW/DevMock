package com.devmock.backend.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "user_achievement")
public class UserAchievement {

    @Id
    @GeneratedValue
    private UUID id;

    private Instant unlockedAt;

    private Boolean isViewed;

    // Relaciones después

    //@ManyToOne
    //private User user;

    //@ManyToOne
    //private Achievement achievement;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    // Getters y Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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