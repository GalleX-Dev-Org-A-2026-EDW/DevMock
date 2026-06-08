package com.devmock.backend.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devmock.backend.dto.AdminDashboardResponse;
import com.devmock.backend.dto.UserResponse;
import com.devmock.backend.entity.User;
import com.devmock.backend.entity.en_enum.SessionStatus;
import com.devmock.backend.entity.en_enum.UserRole;
import com.devmock.backend.repository.AchievementRepository;
import com.devmock.backend.repository.CategoryRepository;
import com.devmock.backend.repository.InterviewSessionRepository;
import com.devmock.backend.repository.QuestionRepository;
import com.devmock.backend.repository.UserRepository;
import com.devmock.backend.service.AdminDashboardService;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final InterviewSessionRepository sessionRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final AchievementRepository achievementRepository;

    public AdminDashboardServiceImpl(
            UserRepository userRepository,
            InterviewSessionRepository sessionRepository,
            QuestionRepository questionRepository,
            CategoryRepository categoryRepository,
            AchievementRepository achievementRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.achievementRepository = achievementRepository;
    }

    @Override
    public AdminDashboardResponse getDashboard() {
        AdminDashboardResponse res = new AdminDashboardResponse();

        long totalUsers = userRepository.countByDeletedAtIsNull();
        res.setTotalUsers(totalUsers);
        res.setActiveUsers(totalUsers);
        res.setStudentsCount(userRepository.countByRoleAndDeletedAtIsNull(UserRole.STUDENT));
        res.setProfessionalsCount(userRepository.countByRoleAndDeletedAtIsNull(UserRole.PROFESSIONAL));
        res.setAdminsCount(userRepository.countByRoleAndDeletedAtIsNull(UserRole.ADMIN));

        res.setTotalSessions(sessionRepository.count());
        res.setSessionsToday(sessionRepository.countByCreatedAtAfter(
                LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)));
        res.setCompletedSessions(sessionRepository.countByStatus(SessionStatus.COMPLETED));
        res.setInProgressSessions(sessionRepository.countByStatus(SessionStatus.IN_PROGRESS));

        res.setTotalQuestions(questionRepository.count());
        res.setActiveQuestions(questionRepository.countByIsActiveTrue());

        res.setTotalCategories(categoryRepository.count());
        res.setActiveCategories(categoryRepository.countByIsActiveTrue());

        res.setTotalAchievements(achievementRepository.count());

        res.setRecentUsers(toUserResponseList(
                userRepository.findTop10ByDeletedAtIsNullOrderByCreatedAtDesc()));

        return res;
    }

    private List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream().map(this::toUserResponse).toList();
    }

    private UserResponse toUserResponse(User user) {
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setEmail(user.getEmail());
        r.setFullName(user.getFullName());
        r.setAvatarUrl(user.getAvatarUrl());
        r.setRole(user.getRole());
        r.setProfessionalExperienceYears(user.getProfessionalExperienceYears());
        r.setIsActive(user.getIsActive());
        r.setIsVerified(user.getIsVerified());
        r.setEmailVerifiedAt(user.getEmailVerifiedAt());
        r.setLastLoginAt(user.getLastLoginAt());
        r.setCreatedAt(user.getCreatedAt());
        r.setUpdatedAt(user.getUpdatedAt());
        if (user.getCurrentLevel() != null) {
            r.setCurrentLevelId(user.getCurrentLevel().getId());
            r.setCurrentLevelName(user.getCurrentLevel().getName());
        }
        return r;
    }
}
