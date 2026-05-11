package com.devmock.backend.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class UpdateCategoryRequest {

    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    @Size(max = 120, message = "slug must be at most 120 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "slug must contain only lowercase letters, numbers and hyphens")
    private String slug;

    private String description;

    private String icon;

    @PositiveOrZero(message = "displayOrder must be zero or positive")
    private Integer displayOrder;

    private Boolean isActive;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
