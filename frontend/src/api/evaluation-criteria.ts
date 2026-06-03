import { http } from "./http"

export interface EvaluationCriterion {
  id: string
  name: string
  slug: string
  description: string | null
  defaultWeight: number
  isActive: boolean
  createdAt: string
}

export interface CreateEvaluationCriterionDto {
  name: string
  slug: string
  description?: string
  defaultWeight: number
}

export interface UpdateEvaluationCriterionDto {
  name?: string
  slug?: string
  description?: string
  defaultWeight?: number
  isActive?: boolean
}

export const evaluationCriteriaApi = {
  list: (activeOnly?: boolean) =>
    http<EvaluationCriterion[]>(
      activeOnly
        ? "/api/evaluation-criteria?activeOnly=true"
        : "/api/evaluation-criteria",
    ),

  getById: (id: string) =>
    http<EvaluationCriterion>(`/api/evaluation-criteria/${id}`),

  getBySlug: (slug: string) =>
    http<EvaluationCriterion>(
      `/api/evaluation-criteria/by-slug/${encodeURIComponent(slug)}`,
    ),

  create: (dto: CreateEvaluationCriterionDto) =>
    http<EvaluationCriterion>("/api/evaluation-criteria", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateEvaluationCriterionDto) =>
    http<EvaluationCriterion>(`/api/evaluation-criteria/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    http<void>(`/api/evaluation-criteria/${id}`, { method: "DELETE" }),
}
