import { http, httpRequired } from "./http"
import type { Achievement } from "./achievements"

export interface SessionQuestion {
  id: string
  questionOrder: number
  assignedTimeSeconds: number | null
  timeUsedSeconds: number | null
  userAnswer: string | null
  obtainedPoints: number | null
  correctnessScore: number | null
  efficiencyScore: number | null
  logicScore: number | null
  clarityScore: number | null
  evaluationFeedback: string | null
  answeredAt: string | null
  sessionId: string | null
  questionId: string | null
  selectedOptionId: string | null
  createdAt: string
  updatedAt: string
  newlyUnlockedAchievements?: Achievement[]
}

export interface CreateSessionQuestionDto {
  questionOrder: number
  assignedTimeSeconds?: number
  timeUsedSeconds?: number
  userAnswer?: string
  obtainedPoints?: number
  correctnessScore?: number
  efficiencyScore?: number
  logicScore?: number
  clarityScore?: number
  evaluationFeedback?: string
  answeredAt?: string
  sessionId?: string
  questionId?: string
  selectedOptionId?: string
}

export interface UpdateSessionQuestionDto {
  questionOrder?: number
  assignedTimeSeconds?: number
  timeUsedSeconds?: number
  userAnswer?: string
  obtainedPoints?: number
  correctnessScore?: number
  efficiencyScore?: number
  logicScore?: number
  clarityScore?: number
  evaluationFeedback?: string
  answeredAt?: string
  sessionId?: string
  questionId?: string
  selectedOptionId?: string
}

export const sessionQuestionsApi = {
  list: () => http<SessionQuestion[]>("/api/session-questions"),

  getById: (id: string) =>
    http<SessionQuestion>(`/api/session-questions/${id}`),

  create: (dto: CreateSessionQuestionDto) =>
    httpRequired<SessionQuestion>("/api/session-questions", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateSessionQuestionDto) =>
    httpRequired<SessionQuestion>(`/api/session-questions/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    httpRequired<void>(`/api/session-questions/${id}`, { method: "DELETE" }),
}
