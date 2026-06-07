package com.devmock.backend.dto;

import java.util.List;

public class AdminDashboardResponse {
    private long totalUsers;
    private long activeUsers;
    private long studentsCount;
    private long professionalsCount;
    private long adminsCount;
    private long totalSessions;
    private long sessionsToday;
    private long completedSessions;
    private long inProgressSessions;
    private long totalQuestions;
    private long activeQuestions;
    private long totalCategories;
    private long activeCategories;
    private long totalAchievements;
    private List<UserResponse> recentUsers;

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getStudentsCount() {
        return studentsCount;
    }

    public void setStudentsCount(long studentsCount) {
        this.studentsCount = studentsCount;
    }

    public long getProfessionalsCount() {
        return professionalsCount;
    }

    public void setProfessionalsCount(long professionalsCount) {
        this.professionalsCount = professionalsCount;
    }

    public long getAdminsCount() {
        return adminsCount;
    }

    public void setAdminsCount(long adminsCount) {
        this.adminsCount = adminsCount;
    }

    public long getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(long totalSessions) {
        this.totalSessions = totalSessions;
    }

    public long getSessionsToday() {
        return sessionsToday;
    }

    public void setSessionsToday(long sessionsToday) {
        this.sessionsToday = sessionsToday;
    }

    public long getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(long completedSessions) {
        this.completedSessions = completedSessions;
    }

    public long getInProgressSessions() {
        return inProgressSessions;
    }

    public void setInProgressSessions(long inProgressSessions) {
        this.inProgressSessions = inProgressSessions;
    }

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public long getActiveQuestions() {
        return activeQuestions;
    }

    public void setActiveQuestions(long activeQuestions) {
        this.activeQuestions = activeQuestions;
    }

    public long getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(long totalCategories) {
        this.totalCategories = totalCategories;
    }

    public long getActiveCategories() {
        return activeCategories;
    }

    public void setActiveCategories(long activeCategories) {
        this.activeCategories = activeCategories;
    }

    public long getTotalAchievements() {
        return totalAchievements;
    }

    public void setTotalAchievements(long totalAchievements) {
        this.totalAchievements = totalAchievements;
    }

    public List<UserResponse> getRecentUsers() {
        return recentUsers;
    }

    public void setRecentUsers(List<UserResponse> recentUsers) {
        this.recentUsers = recentUsers;
    }
}
