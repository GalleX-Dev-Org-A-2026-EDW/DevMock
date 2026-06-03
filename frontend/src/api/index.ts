export { http, httpRequired, ApiError, getToken, setToken, removeToken, clearAuth } from "./http"
export type { ApiError as ApiErrorType } from "./http"

export type {
  UserRole,
  SessionStatus,
  QuestionType,
  AnswerFormat,
  AuditAction,
  RankingPeriod,
} from "./enums"

export { authApi } from "./auth"
export type { LoginRequest, RegisterRequest, AuthResponse } from "./auth"

export { usersApi } from "./users"
export type { User, CreateUserDto, UpdateUserDto } from "./users"

export { questionsApi } from "./questions"
export type { Question, CreateQuestionDto, UpdateQuestionDto } from "./questions"

export { answerOptionsApi } from "./answer-options"
export type { AnswerOption, CreateAnswerOptionDto, UpdateAnswerOptionDto } from "./answer-options"

export { questionCriteriaApi } from "./question-criteria"
export type { QuestionCriterion, CreateQuestionCriterionDto, UpdateQuestionCriterionDto } from "./question-criteria"

export { categoriesApi } from "./categories"
export type { Category, CreateCategoryDto, UpdateCategoryDto } from "./categories"

export { difficultyLevelsApi } from "./difficulty-levels"
export type { DifficultyLevel, CreateDifficultyLevelDto, UpdateDifficultyLevelDto } from "./difficulty-levels"

export { interviewTypesApi } from "./interview-types"
export type { InterviewType, CreateInterviewTypeDto, UpdateInterviewTypeDto } from "./interview-types"

export { evaluationCriteriaApi } from "./evaluation-criteria"
export type { EvaluationCriterion, CreateEvaluationCriterionDto, UpdateEvaluationCriterionDto } from "./evaluation-criteria"

export { interviewSessionsApi } from "./interview-sessions"
export type { InterviewSession, CreateInterviewSessionDto, UpdateInterviewSessionDto } from "./interview-sessions"

export { sessionQuestionsApi } from "./session-questions"
export type { SessionQuestion, CreateSessionQuestionDto, UpdateSessionQuestionDto } from "./session-questions"

export { userPerformancesApi } from "./user-performances"
export type { UserPerformance, CreateUserPerformanceDto, UpdateUserPerformanceDto } from "./user-performances"

export { rankingsApi } from "./rankings"
export type { Ranking, CreateRankingDto, UpdateRankingDto } from "./rankings"

export { achievementsApi } from "./achievements"
export type { Achievement, CreateAchievementDto, UpdateAchievementDto } from "./achievements"

export { userAchievementsApi } from "./user-achievements"
export type { UserAchievement, CreateUserAchievementDto, UpdateUserAchievementDto } from "./user-achievements"

export { auditLogsApi } from "./audit-logs"
export type { AuditLog, CreateAuditLogDto, UpdateAuditLogDto } from "./audit-logs"

/* ─── Query Keys ─── */
export { usersKeys } from "./users.keys"
export { questionsKeys } from "./questions.keys"
export { categoriesKeys } from "./categories.keys"
export { answerOptionsKeys } from "./answer-options.keys"
export { questionCriteriaKeys } from "./question-criteria.keys"
export { difficultyLevelsKeys } from "./difficulty-levels.keys"
export { interviewTypesKeys } from "./interview-types.keys"
export { evaluationCriteriaKeys } from "./evaluation-criteria.keys"
export { interviewSessionsKeys } from "./interview-sessions.keys"
export { sessionQuestionsKeys } from "./session-questions.keys"
export { userPerformancesKeys } from "./user-performances.keys"
export { rankingsKeys } from "./rankings.keys"
export { achievementsKeys } from "./achievements.keys"
export { userAchievementsKeys } from "./user-achievements.keys"
export { auditLogsKeys } from "./audit-logs.keys"

/* ─── Query & Mutation Hooks ─── */
export { useLogin, useRegister } from "./auth.queries"
export { useUsers, useUser, useUserByEmail, useCreateUser, useUpdateUser, useDeleteUser } from "./users.queries"
export { useQuestions, useQuestion, useCreateQuestion, useUpdateQuestion, useDeleteQuestion } from "./questions.queries"
export { useCategories, useCategory, useCategoryBySlug, useCreateCategory, useUpdateCategory, useDeleteCategory } from "./categories.queries"
export { useAnswerOptions, useAnswerOption, useCreateAnswerOption, useUpdateAnswerOption, useDeleteAnswerOption } from "./answer-options.queries"
export { useQuestionCriteria, useQuestionCriterion, useCreateQuestionCriterion, useUpdateQuestionCriterion, useDeleteQuestionCriterion } from "./question-criteria.queries"
export { useDifficultyLevels, useDifficultyLevel, useDifficultyLevelBySlug, useCreateDifficultyLevel, useUpdateDifficultyLevel, useDeleteDifficultyLevel } from "./difficulty-levels.queries"
export { useInterviewTypes, useInterviewType, useInterviewTypeBySlug, useCreateInterviewType, useUpdateInterviewType, useDeleteInterviewType } from "./interview-types.queries"
export { useEvaluationCriteria, useEvaluationCriterion, useEvaluationCriterionBySlug, useCreateEvaluationCriterion, useUpdateEvaluationCriterion, useDeleteEvaluationCriterion } from "./evaluation-criteria.queries"
export { useInterviewSessions, useInterviewSession, useCreateInterviewSession, useUpdateInterviewSession, useDeleteInterviewSession } from "./interview-sessions.queries"
export { useSessionQuestions, useSessionQuestion, useCreateSessionQuestion, useUpdateSessionQuestion, useDeleteSessionQuestion } from "./session-questions.queries"
export { useUserPerformances, useUserPerformance, useCreateUserPerformance, useUpdateUserPerformance, useDeleteUserPerformance } from "./user-performances.queries"
export { useRankings, useRanking, useCreateRanking, useUpdateRanking, useDeleteRanking } from "./rankings.queries"
export { useAchievements, useAchievement, useCreateAchievement, useUpdateAchievement, useDeleteAchievement } from "./achievements.queries"
export { useUserAchievements, useUserAchievement, useCreateUserAchievement, useUpdateUserAchievement, useDeleteUserAchievement } from "./user-achievements.queries"
export { useAuditLogs, useAuditLog, useCreateAuditLog, useUpdateAuditLog, useDeleteAuditLog } from "./audit-logs.queries"
