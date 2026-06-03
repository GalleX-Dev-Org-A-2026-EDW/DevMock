import { http } from "./http"

export interface UserAchievement {
  id: string
  userId: string
  achievementId: string
  unlockedAt: string
  isViewed: boolean
  createdAt: string
}

export interface CreateUserAchievementDto {
  userId: string
  achievementId: string
  unlockedAt: string
  isViewed: boolean
}

export interface UpdateUserAchievementDto {
  userId?: string
  achievementId?: string
  unlockedAt?: string
  isViewed?: boolean
}

export const userAchievementsApi = {
  list: () => http<UserAchievement[]>("/api/user-achievements"),

  getById: (id: string) =>
    http<UserAchievement>(`/api/user-achievements/${id}`),

  create: (dto: CreateUserAchievementDto) =>
    http<UserAchievement>("/api/user-achievements", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateUserAchievementDto) =>
    http<UserAchievement>(`/api/user-achievements/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    http<void>(`/api/user-achievements/${id}`, { method: "DELETE" }),
}
