package com.devmock.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devmock.backend.entity.Achievement;

public interface AchievementRepository
        extends JpaRepository<Achievement, UUID> {

}