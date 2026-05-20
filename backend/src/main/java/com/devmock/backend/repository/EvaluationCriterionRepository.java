package com.devmock.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devmock.backend.entity.EvaluationCriterion;

public interface EvaluationCriterionRepository extends JpaRepository<EvaluationCriterion, UUID> {

    Optional<EvaluationCriterion> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<EvaluationCriterion> findByIsActiveTrue();
}
