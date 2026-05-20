package com.devmock.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devmock.backend.entity.InterviewType;
import com.devmock.backend.entity.en_enum.QuestionType;

public interface InterviewTypeRepository extends JpaRepository<InterviewType, UUID> {

    Optional<InterviewType> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<InterviewType> findByIsActiveTrue();

    List<InterviewType> findByQuestionType(QuestionType questionType);
}
