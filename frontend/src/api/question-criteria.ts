import { http, httpRequired } from "./http"

export interface QuestionCriterion {
  id: string
  questionId: string
  criterionId: string
  weight: number
}

export interface CreateQuestionCriterionDto {
  questionId: string
  criterionId: string
  weight: number
}

export interface UpdateQuestionCriterionDto {
  weight?: number
}

export const questionCriteriaApi = {
  list: () => http<QuestionCriterion[]>("/api/question-criteria"),

  getById: (id: string) =>
    http<QuestionCriterion>(`/api/question-criteria/${id}`),

  create: (dto: CreateQuestionCriterionDto) =>
    httpRequired<QuestionCriterion>("/api/question-criteria", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateQuestionCriterionDto) =>
    httpRequired<QuestionCriterion>(`/api/question-criteria/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    httpRequired<void>(`/api/question-criteria/${id}`, { method: "DELETE" }),
}
