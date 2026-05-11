package com.devmock.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import com.devmock.backend.entity.SessionQuestion;

public interface SessionQuestionRepository extends JpaRepository<SessionQuestion, UUID> {
    
}
