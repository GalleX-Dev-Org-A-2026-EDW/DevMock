import { http, httpRequired } from "./http"

export interface Achievement {
  id: string
  name: string
  slug: string
  description: string | null
  iconUrl: string | null
  unlockCriteria: string | null
  pointsReward: number | null
  isActive: boolean
  createdAt: string
}

export interface CreateAchievementDto {
  name: string
  slug: string
  description?: string
  iconUrl?: string
  unlockCriteria?: string
  pointsReward?: number
  isActive: boolean
}

export interface UpdateAchievementDto {
  name?: string
  slug?: string
  description?: string
  iconUrl?: string
  unlockCriteria?: string
  pointsReward?: number
  isActive?: boolean
}

export const achievementsApi = {
  list: () => http<Achievement[]>("/api/achievements"),

  getById: (id: string) => http<Achievement>(`/api/achievements/${id}`),

  create: (dto: CreateAchievementDto) =>
    httpRequired<Achievement>("/api/achievements", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateAchievementDto) =>
    httpRequired<Achievement>(`/api/achievements/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    httpRequired<void>(`/api/achievements/${id}`, { method: "DELETE" }),
}
