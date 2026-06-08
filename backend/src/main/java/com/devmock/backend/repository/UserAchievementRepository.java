package com.devmock.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.devmock.backend.entity.Achievement;
import com.devmock.backend.entity.User;
import com.devmock.backend.entity.UserAchievement;

public interface UserAchievementRepository
        extends JpaRepository<UserAchievement, UUID> {

    List<UserAchievement> findByUser(User user);
    List<UserAchievement> findByUserIsNull();
    Optional<UserAchievement> findByUserAndAchievement(User user, Achievement achievement);
}