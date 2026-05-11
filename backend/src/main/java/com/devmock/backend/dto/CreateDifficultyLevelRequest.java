package com.devmock.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CreateDifficultyLevelRequest {

    @NotBlank(message = "name is required")
    @Size(max = 50, message = "name must be at most 50 characters")
    private String name;

    @NotBlank(message = "slug is required")
    @Size(max = 60, message = "slug must be at most 60 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "slug must contain only lowercase letters, numbers and hyphens")
    private String slug;

    @NotNull(message = "levelOrder is required")
    @Positive(message = "levelOrder must be positive")
    private Integer levelOrder;

    @NotNull(message = "pointsMultiplier is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "pointsMultiplier must be zero or positive")
    @Digits(integer = 2, fraction = 2, message = "pointsMultiplier must have at most 2 integer digits and 2 decimals")
    private BigDecimal pointsMultiplier;

    private String description;

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
}
