package com.devmock.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devmock.backend.entity.UserAchievement;

public interface UserAchievementRepository
        extends JpaRepository<UserAchievement, UUID> {

}