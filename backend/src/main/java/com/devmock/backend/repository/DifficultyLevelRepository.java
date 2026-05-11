package com.devmock.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devmock.backend.entity.DifficultyLevel;

public interface DifficultyLevelRepository extends JpaRepository<DifficultyLevel, UUID> {

    Optional<DifficultyLevel> findBySlug(String slug);

    Optional<DifficultyLevel> findByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    boolean existsByLevelOrder(Integer levelOrder);

    List<DifficultyLevel> findAllByOrderByLevelOrderAsc();
}
