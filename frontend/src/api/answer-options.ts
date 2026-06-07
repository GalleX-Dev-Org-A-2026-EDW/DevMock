import { http, httpRequired } from "./http"

export interface AnswerOption {
  id: string
  questionId: string
  optionText: string
  isCorrect: boolean
  explanation: string | null
  displayOrder: number | null
  createdAt: string
}

export interface CreateAnswerOptionDto {
  questionId: string
  optionText: string
  isCorrect: boolean
  explanation?: string
  displayOrder?: number
}

export interface UpdateAnswerOptionDto {
  questionId?: string
  optionText?: string
  isCorrect?: boolean
  explanation?: string
  displayOrder?: number
}

export const answerOptionsApi = {
  list: () => http<AnswerOption[]>("/api/answer-options"),

  getById: (id: string) => http<AnswerOption>(`/api/answer-options/${id}`),

  create: (dto: CreateAnswerOptionDto) =>
    httpRequired<AnswerOption>("/api/answer-options", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateAnswerOptionDto) =>
    httpRequired<AnswerOption>(`/api/answer-options/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    httpRequired<void>(`/api/answer-options/${id}`, { method: "DELETE" }),
}
