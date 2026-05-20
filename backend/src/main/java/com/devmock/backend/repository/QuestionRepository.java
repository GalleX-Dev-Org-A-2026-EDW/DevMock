package com.devmock.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devmock.backend.entity.Question;
import com.devmock.backend.entity.en_enum.QuestionType;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    List<Question> findByIsActiveTrue();

    List<Question> findByQuestionType(QuestionType questionType);
}
