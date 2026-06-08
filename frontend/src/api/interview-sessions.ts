import { http, httpRequired } from "./http"
import type { Achievement } from "./achievements"
import type { SessionStatus } from "./enums"

export interface InterviewSession {
  id: string
  status: SessionStatus
  startedAt: string | null
  finishedAt: string | null
  totalTimeUsedSeconds: number | null
  finalScore: number | null
  correctnessScore: number | null
  efficiencyScore: number | null
  logicScore: number | null
  clarityScore: number | null
  userId: string | null
  interviewTypeId: string | null
  difficultyId: string | null
  categoryId: string | null
  createdAt: string
  updatedAt: string
  newlyUnlockedAchievements?: Achievement[]
}

export interface CreateInterviewSessionDto {
  status: SessionStatus
  startedAt?: string
  finishedAt?: string
  totalTimeUsedSeconds?: number
  finalScore?: number
  correctnessScore?: number
  efficiencyScore?: number
  logicScore?: number
  clarityScore?: number
  userId?: string
  interviewTypeId?: string
  difficultyId?: string
  categoryId?: string
}

export interface UpdateInterviewSessionDto {
  status?: SessionStatus
  startedAt?: string
  finishedAt?: string
  totalTimeUsedSeconds?: number
  finalScore?: number
  correctnessScore?: number
  efficiencyScore?: number
  logicScore?: number
  clarityScore?: number
  userId?: string
  interviewTypeId?: string
  difficultyId?: string
  categoryId?: string
}

export const interviewSessionsApi = {
  list: () => http<InterviewSession[]>("/api/interview-sessions"),

  getById: (id: string) =>
    http<InterviewSession>(`/api/interview-sessions/${id}`),

  create: (dto: CreateInterviewSessionDto) =>
    httpRequired<InterviewSession>("/api/interview-sessions", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateInterviewSessionDto) =>
    httpRequired<InterviewSession>(`/api/interview-sessions/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    httpRequired<void>(`/api/interview-sessions/${id}`, { method: "DELETE" }),
}
