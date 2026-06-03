import { http } from "./http"

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
    http<SessionQuestion>("/api/session-questions", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateSessionQuestionDto) =>
    http<SessionQuestion>(`/api/session-questions/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    http<void>(`/api/session-questions/${id}`, { method: "DELETE" }),
}
