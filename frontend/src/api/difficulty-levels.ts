import { http, httpRequired } from "./http"

export interface DifficultyLevel {
  id: string
  name: string
  slug: string
  levelOrder: number
  pointsMultiplier: number
  description: string | null
  createdAt: string
}

export interface CreateDifficultyLevelDto {
  name: string
  slug: string
  levelOrder: number
  pointsMultiplier: number
  description?: string
}

export interface UpdateDifficultyLevelDto {
  name?: string
  slug?: string
  levelOrder?: number
  pointsMultiplier?: number
  description?: string
}

export const difficultyLevelsApi = {
  list: () => http<DifficultyLevel[]>("/api/difficulty-levels"),

  getById: (id: string) =>
    http<DifficultyLevel>(`/api/difficulty-levels/${id}`),

  getBySlug: (slug: string) =>
    http<DifficultyLevel>(
      `/api/difficulty-levels/by-slug/${encodeURIComponent(slug)}`,
    ),

  create: (dto: CreateDifficultyLevelDto) =>
    httpRequired<DifficultyLevel>("/api/difficulty-levels", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateDifficultyLevelDto) =>
    httpRequired<DifficultyLevel>(`/api/difficulty-levels/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    httpRequired<void>(`/api/difficulty-levels/${id}`, { method: "DELETE" }),
}
