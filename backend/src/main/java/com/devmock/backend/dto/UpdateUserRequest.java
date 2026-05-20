package com.devmock.backend.dto;

import com.devmock.backend.entity.en_enum.UserRole;
import java.util.UUID;

public class UpdateUserRequest {
    private String fullName;
    private String avatarUrl;
    private UserRole role;
    private Integer professionalExperienceYears;
    private Boolean isActive;
    private Boolean isVerified;
    private String password;

    private UUID currentLevelId;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Integer getProfessionalExperienceYears() {
        return professionalExperienceYears;
    }

    public void setProfessionalExperienceYears(Integer professionalExperienceYears) {
        this.professionalExperienceYears = professionalExperienceYears;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getCurrentLevelId() {
        return currentLevelId;
    }

    public void setCurrentLevelId(UUID currentLevelId) {
        this.currentLevelId = currentLevelId;
    }
}