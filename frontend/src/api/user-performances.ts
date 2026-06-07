import { http, httpRequired } from "./http"

export interface UserPerformance {
  id: string
  totalQuestionsAnswered: number
  totalCorrect: number | null
  accuracyPercentage: number | null
  avgTimeSeconds: number | null
  avgScore: number | null
  strengths: string | null
  weaknesses: string | null
  lastPracticedAt: string | null
  userId: string | null
  categoryId: string | null
  difficultyId: string | null
  updatedAt: string
}

export interface CreateUserPerformanceDto {
  totalQuestionsAnswered: number
  totalCorrect?: number
  accuracyPercentage?: number
  avgTimeSeconds?: number
  avgScore?: number
  strengths?: string
  weaknesses?: string
  lastPracticedAt?: string
  userId?: string
  categoryId?: string
  difficultyId?: string
}

export interface UpdateUserPerformanceDto {
  totalQuestionsAnswered?: number
  totalCorrect?: number
  accuracyPercentage?: number
  avgTimeSeconds?: number
  avgScore?: number
  strengths?: string
  weaknesses?: string
  lastPracticedAt?: string
  userId?: string
  categoryId?: string
  difficultyId?: string
}

export const userPerformancesApi = {
  list: () => http<UserPerformance[]>("/api/user-performances"),

  getById: (id: string) =>
    http<UserPerformance>(`/api/user-performances/${id}`),

  create: (dto: CreateUserPerformanceDto) =>
    httpRequired<UserPerformance>("/api/user-performances", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateUserPerformanceDto) =>
    httpRequired<UserPerformance>(`/api/user-performances/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    httpRequired<void>(`/api/user-performances/${id}`, { method: "DELETE" }),
}
