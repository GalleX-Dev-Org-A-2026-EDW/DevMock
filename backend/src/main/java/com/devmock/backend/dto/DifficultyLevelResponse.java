package com.devmock.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class DifficultyLevelResponse {

    private UUID id;
    private String name;
    private String slug;
    private Integer levelOrder;
    private BigDecimal pointsMultiplier;
    private String description;
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getLevelOrder() {
        return levelOrder;
    }

    public void setLevelOrder(Integer levelOrder) {
        this.levelOrder = levelOrder;
    }

    public BigDecimal getPointsMultiplier() {
        return pointsMultiplier;
    }

    public void setPointsMultiplier(BigDecimal pointsMultiplier) {
        this.pointsMultiplier = pointsMultiplier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
