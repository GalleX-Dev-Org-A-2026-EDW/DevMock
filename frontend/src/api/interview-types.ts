import { http } from "./http"
import type { QuestionType } from "./enums"

export interface InterviewType {
  id: string
  name: string
  slug: string
  questionType: QuestionType
  totalQuestions: number
  totalTimeSeconds: number
  description: string | null
  isActive: boolean
  createdAt: string
}

export interface CreateInterviewTypeDto {
  name: string
  slug: string
  questionType: QuestionType
  totalQuestions: number
  totalTimeSeconds: number
  description?: string
}

export interface UpdateInterviewTypeDto {
  name?: string
  slug?: string
  questionType?: QuestionType
  totalQuestions?: number
  totalTimeSeconds?: number
  description?: string
  isActive?: boolean
}

export const interviewTypesApi = {
  list: (activeOnly?: boolean) =>
    http<InterviewType[]>(
      activeOnly
        ? "/api/interview-types?activeOnly=true"
        : "/api/interview-types",
    ),

  getById: (id: string) =>
    http<InterviewType>(`/api/interview-types/${id}`),

  getBySlug: (slug: string) =>
    http<InterviewType>(
      `/api/interview-types/by-slug/${encodeURIComponent(slug)}`,
    ),

  create: (dto: CreateInterviewTypeDto) =>
    http<InterviewType>("/api/interview-types", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateInterviewTypeDto) =>
    http<InterviewType>(`/api/interview-types/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    http<void>(`/api/interview-types/${id}`, { method: "DELETE" }),
}
