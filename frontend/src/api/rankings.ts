import { http, httpRequired } from "./http"
import type { RankingPeriod } from "./enums"

export interface Ranking {
  id: string
  period: RankingPeriod
  periodStartDate: string | null
  periodEndDate: string | null
  totalScore: number | null
  totalSessions: number | null
  rankPosition: number | null
  calculatedAt: string | null
  userId: string | null
  categoryId: string | null
  difficultyId: string | null
}

export interface CreateRankingDto {
  period: RankingPeriod
  periodStartDate?: string
  periodEndDate?: string
  totalScore?: number
  totalSessions?: number
  rankPosition?: number
  calculatedAt?: string
  userId?: string
  categoryId?: string
  difficultyId?: string
}

export interface UpdateRankingDto {
  period?: RankingPeriod
  periodStartDate?: string
  periodEndDate?: string
  totalScore?: number
  totalSessions?: number
  rankPosition?: number
  calculatedAt?: string
  userId?: string
  categoryId?: string
  difficultyId?: string
}

export const rankingsApi = {
  list: () => http<Ranking[]>("/api/rankings"),

  getById: (id: string) => http<Ranking>(`/api/rankings/${id}`),

  create: (dto: CreateRankingDto) =>
    httpRequired<Ranking>("/api/rankings", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateRankingDto) =>
    httpRequired<Ranking>(`/api/rankings/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    httpRequired<void>(`/api/rankings/${id}`, { method: "DELETE" }),
}
