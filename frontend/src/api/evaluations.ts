import { httpRequired } from "./http"

export interface CodeEvaluationRequest {
  code: string
  expectedAnswer: string | null
  evaluationConfig: string | null
  estimatedTimeSeconds: number | null
  timeUsedSeconds: number
  basePoints: number
}

export interface CodeEvaluationResult {
  correctnessScore: number
  efficiencyScore: number
  logicScore: number
  clarityScore: number
  obtainedPoints: number
  evaluationFeedback: string
  compilationOutput: string | null
}

export const evaluationsApi = {
  evaluateCode: (dto: CodeEvaluationRequest) =>
    httpRequired<CodeEvaluationResult>("/api/evaluations/code", {
      method: "POST",
      body: JSON.stringify(dto),
    }),
}
