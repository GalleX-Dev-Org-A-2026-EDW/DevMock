package com.devmock.backend.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import com.devmock.backend.dto.CreateRankingRequest;
import com.devmock.backend.dto.UpdateRankingRequest;
import com.devmock.backend.dto.RankingResponse;
import com.devmock.backend.entity.Category;
import com.devmock.backend.entity.DifficultyLevel;
import com.devmock.backend.entity.InterviewSession;
import com.devmock.backend.entity.Ranking;
import com.devmock.backend.entity.User;
import com.devmock.backend.entity.en_enum.SessionStatus;
import com.devmock.backend.entity.en_enum.RankingPeriod;
import com.devmock.backend.exception.ResourceNotFoundException;
import com.devmock.backend.repository.CategoryRepository;
import com.devmock.backend.repository.DifficultyLevelRepository;
import com.devmock.backend.repository.InterviewSessionRepository;
import com.devmock.backend.repository.RankingRepository;
import com.devmock.backend.repository.UserRepository;
import com.devmock.backend.service.RankingService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RankingServiceImpl implements RankingService {

    private final RankingRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DifficultyLevelRepository difficultyLevelRepository;
    private final InterviewSessionRepository sessionRepository;

    public RankingServiceImpl(RankingRepository repository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            DifficultyLevelRepository difficultyLevelRepository,
            InterviewSessionRepository sessionRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.difficultyLevelRepository = difficultyLevelRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public RankingResponse create(CreateRankingRequest request) {
        Ranking r = new Ranking();

        r.setPeriod(request.getPeriod());
        r.setPeriodStartDate(request.getPeriodStartDate());
        r.setPeriodEndDate(request.getPeriodEndDate());
        r.setTotalScore(request.getTotalScore());
        r.setTotalSessions(request.getTotalSessions());
        r.setRankPosition(request.getRankPosition());
        r.setCalculatedAt(request.getCalculatedAt());

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User " + request.getUserId() + " not found"));
            r.setUser(user);
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category " + request.getCategoryId() + " not found"));
            r.setCategory(category);
        }
        if (request.getDifficultyId() != null) {
            DifficultyLevel level = difficultyLevelRepository.findById(request.getDifficultyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DifficultyLevel " + request.getDifficultyId() + " not found"));
            r.setDifficulty(level);
        }

        Ranking saved = repository.save(r);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankingResponse> list() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RankingResponse getById(UUID id) {
        Ranking r = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ranking " + id + " not found"));
        return toResponse(r);
    }

    @Override
    public RankingResponse update(UUID id, UpdateRankingRequest request) {
        Ranking r = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ranking " + id + " not found"));

        if (request.getPeriod() != null) r.setPeriod(request.getPeriod());
        if (request.getPeriodStartDate() != null) r.setPeriodStartDate(request.getPeriodStartDate());
        if (request.getPeriodEndDate() != null) r.setPeriodEndDate(request.getPeriodEndDate());
        if (request.getTotalScore() != null) r.setTotalScore(request.getTotalScore());
        if (request.getTotalSessions() != null) r.setTotalSessions(request.getTotalSessions());
        if (request.getRankPosition() != null) r.setRankPosition(request.getRankPosition());
        if (request.getCalculatedAt() != null) r.setCalculatedAt(request.getCalculatedAt());

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User " + request.getUserId() + " not found"));
            r.setUser(user);
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category " + request.getCategoryId() + " not found"));
            r.setCategory(category);
        }
        if (request.getDifficultyId() != null) {
            DifficultyLevel level = difficultyLevelRepository.findById(request.getDifficultyId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DifficultyLevel " + request.getDifficultyId() + " not found"));
            r.setDifficulty(level);
        }

        return toResponse(repository.save(r));
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Ranking " + id + " not found");
        }
        repository.deleteById(id);
    }

    @Override
    public void recalculate() {
        List<Ranking> global = repository.findAll().stream()
                .filter(r -> r.getCategory() == null && r.getDifficulty() == null)
                .toList();
        repository.deleteAll(global);

        List<InterviewSession> completed = sessionRepository
                .findByStatusAndFinalScoreIsNotNull(SessionStatus.COMPLETED);

        Map<UUID, List<InterviewSession>> byUser = completed.stream()
                .filter(s -> s.getUser() != null)
                .collect(Collectors.groupingBy(s -> s.getUser().getId()));

        Instant now = Instant.now();
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Map<UUID, Aggregated> allTime = new HashMap<>();
        Map<UUID, Aggregated> monthly = new HashMap<>();
        Map<UUID, Aggregated> weekly = new HashMap<>();

        for (Map.Entry<UUID, List<InterviewSession>> entry : byUser.entrySet()) {
            UUID uid = entry.getKey();
            List<InterviewSession> sessions = entry.getValue();

            allTime.put(uid, aggregate(sessions));

            List<InterviewSession> monthSessions = sessions.stream()
                    .filter(s -> s.getFinishedAt() != null)
                    .filter(s -> {
                        LocalDate d = LocalDate.ofInstant(s.getFinishedAt(), ZoneId.systemDefault());
                        return !d.isBefore(monthStart);
                    })
                    .toList();
            if (!monthSessions.isEmpty()) {
                monthly.put(uid, aggregate(monthSessions));
            }

            List<InterviewSession> weekSessions = sessions.stream()
                    .filter(s -> s.getFinishedAt() != null)
                    .filter(s -> {
                        LocalDate d = LocalDate.ofInstant(s.getFinishedAt(), ZoneId.systemDefault());
                        return !d.isBefore(weekStart);
                    })
                    .toList();
            if (!weekSessions.isEmpty()) {
                weekly.put(uid, aggregate(weekSessions));
            }
        }

        persistGlobalRankings(allTime, RankingPeriod.ALL_TIME, null, null, now);
        persistGlobalRankings(monthly, RankingPeriod.MONTHLY, monthStart, monthStart.plusMonths(1), now);
        persistGlobalRankings(weekly, RankingPeriod.WEEKLY, weekStart, weekStart.plusWeeks(1), now);
    }

    private Aggregated aggregate(List<InterviewSession> sessions) {
        BigDecimal avg = sessions.stream()
                .map(s -> s.getFinalScore() != null ? s.getFinalScore() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(sessions.size()), 2, RoundingMode.HALF_UP);
        return new Aggregated(avg, sessions.size());
    }

    private void persistGlobalRankings(Map<UUID, Aggregated> data, RankingPeriod period,
            LocalDate periodStart, LocalDate periodEnd, Instant calculatedAt) {
        List<Map.Entry<UUID, Aggregated>> sorted = data.entrySet().stream()
                .sorted((a, b) -> b.getValue().score.compareTo(a.getValue().score))
                .toList();

        int rank = 1;
        for (Map.Entry<UUID, Aggregated> entry : sorted) {
            User user = userRepository.getReferenceById(entry.getKey());
            Aggregated a = entry.getValue();

            Ranking r = new Ranking();
            r.setPeriod(period);
            r.setPeriodStartDate(periodStart);
            r.setPeriodEndDate(periodEnd);
            r.setTotalScore(a.score);
            r.setTotalSessions(a.sessions);
            r.setRankPosition(rank++);
            r.setCalculatedAt(calculatedAt);
            r.setUser(user);
            repository.save(r);
        }
    }

    private record Aggregated(BigDecimal score, int sessions) {}

    private RankingResponse toResponse(Ranking r) {
        RankingResponse response = new RankingResponse();

        response.setId(r.getId());
        response.setPeriod(r.getPeriod());
        response.setPeriodStartDate(r.getPeriodStartDate());
        response.setPeriodEndDate(r.getPeriodEndDate());
        response.setTotalScore(r.getTotalScore());
        response.setTotalSessions(r.getTotalSessions());
        response.setRankPosition(r.getRankPosition());
        response.setCalculatedAt(r.getCalculatedAt());

        if (r.getUser() != null) {
            response.setUserId(r.getUser().getId());
            response.setUserName(r.getUser().getFullName());
            response.setUserRole(r.getUser().getRole());
        }
        if (r.getCategory() != null) response.setCategoryId(r.getCategory().getId());
        if (r.getDifficulty() != null) response.setDifficultyId(r.getDifficulty().getId());

        return response;
    }
}
