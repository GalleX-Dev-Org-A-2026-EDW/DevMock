import { http, httpRequired } from "./http"
import type { QuestionType, AnswerFormat } from "./enums"

export interface AnswerOptionDto {
  id?: string
  optionText: string
  isCorrect: boolean
  explanation?: string
  displayOrder?: number
}

export interface Question {
  id: string
  questionType: QuestionType
  answerFormat: AnswerFormat
  statement: string
  expectedAnswer: string | null
  explanation: string | null
  estimatedTimeSeconds: number | null
  basePoints: number
  evaluationConfig: string | null
  tags: string[]
  isActive: boolean
  createdById: string | null
  categoryId: string | null
  difficultyId: string | null
  createdAt: string
  updatedAt: string
  answerOptions: AnswerOptionDto[]
}

export interface CreateQuestionDto {
  questionType: QuestionType
  answerFormat: AnswerFormat
  statement: string
  expectedAnswer?: string
  explanation?: string
  estimatedTimeSeconds?: number
  basePoints: number
  evaluationConfig?: string
  tags?: string[]
  createdById?: string
  categoryId?: string
  difficultyId?: string
  answerOptions?: AnswerOptionDto[]
}

export interface UpdateQuestionDto {
  questionType?: QuestionType
  answerFormat?: AnswerFormat
  statement?: string
  expectedAnswer?: string
  explanation?: string
  estimatedTimeSeconds?: number
  basePoints?: number
  evaluationConfig?: string
  tags?: string[]
  isActive?: boolean
  createdById?: string
  categoryId?: string
  difficultyId?: string
  answerOptions?: AnswerOptionDto[]
}

export const questionsApi = {
  list: () => http<Question[]>("/api/questions"),

  getById: (id: string) => http<Question>(`/api/questions/${id}`),

  create: (dto: CreateQuestionDto) =>
    httpRequired<Question>("/api/questions", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateQuestionDto) =>
    httpRequired<Question>(`/api/questions/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    httpRequired<void>(`/api/questions/${id}`, { method: "DELETE" }),
}
