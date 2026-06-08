package com.devmock.backend.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devmock.backend.entity.InterviewSession;
import com.devmock.backend.entity.User;
import com.devmock.backend.entity.en_enum.SessionStatus;

public interface InterviewSessionRepository extends JpaRepository<InterviewSession, UUID> {
    long countByStatus(SessionStatus status);
    long countByCreatedAtAfter(Instant after);
    List<InterviewSession> findByStatusAndFinalScoreIsNotNull(SessionStatus status);
    List<InterviewSession> findByUser(User user);
    List<InterviewSession> findByUserIsNull();
    long countByUserAndStatus(User user, SessionStatus status);
}
