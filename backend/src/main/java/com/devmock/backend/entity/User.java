package com.devmock.backend.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.Instant;

import com.devmock.backend.entity.en_enum.UserRole;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 120)
    private String fullName;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private Integer professionalExperienceYears;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isVerified;

    private Instant emailVerifiedAt;

    private Instant lastLoginAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Instant deletedAt;

    @ManyToOne
    @JoinColumn(name = "current_level_id")
    private DifficultyLevel currentLevel;

    @OneToMany(mappedBy = "user")
    private List<InterviewSession> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserPerformance> performances = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Ranking> rankings = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserAchievement> achievements = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    private List<Question> createdQuestions = new ArrayList<>();

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (isActive == null) isActive = true;
        if (isVerified == null) isVerified = false;
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }
    
    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

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

    public Instant getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(Instant emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public DifficultyLevel getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(DifficultyLevel currentLevel) {
        this.currentLevel = currentLevel;
    }

    public List<InterviewSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<InterviewSession> sessions) {
        this.sessions = sessions;
    }

    public List<UserPerformance> getPerformances() {
        return performances;
    }

    public void setPerformances(List<UserPerformance> performances) {
        this.performances = performances;
    }

    public List<Ranking> getRankings() {
        return rankings;
    }

    public void setRankings(List<Ranking> rankings) {
        this.rankings = rankings;
    }

    public List<UserAchievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<UserAchievement> achievements) {
        this.achievements = achievements;
    }

    public List<Question> getCreatedQuestions() {
        return createdQuestions;
    }

    public void setCreatedQuestions(List<Question> createdQuestions) {
        this.createdQuestions = createdQuestions;
    }
}
