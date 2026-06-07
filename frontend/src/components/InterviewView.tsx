import { useCallback, useEffect, useMemo, useState } from "react"
import { useSessionQuestions, useUpdateSessionQuestion } from "@/api/session-questions.queries"
import { useUpdateInterviewSession } from "@/api/interview-sessions.queries"
import { useQuestions } from "@/api/questions.queries"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import type { Question } from "@/api/questions"

type Props = {
  sessionId: string
  onFinish: (sessionId: string) => void
  onCancel: () => void
}

type Evaluation = {
  obtainedPoints: number
  correctnessScore: number
  efficiencyScore: number
  logicScore: number
  clarityScore: number
  feedback: string
}

function normalize(text: string) {
  return text
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-z0-9#]+/g, " ")
    .trim()
}

function tokenSet(text: string) {
  return new Set(normalize(text).split(/\s+/).filter((token) => token.length > 2))
}

function evaluateAnswer(question: Question | undefined, answer: string, timeUsedSeconds: number): Evaluation {
  const cleanAnswer = answer.trim()
  const reference = question?.expectedAnswer ?? question?.explanation ?? ""
  const answerTokens = tokenSet(cleanAnswer)
  const referenceTokens = tokenSet(reference)

  let correctnessScore = cleanAnswer ? 45 : 0
  if (referenceTokens.size > 0 && answerTokens.size > 0) {
    const matches = [...answerTokens].filter((token) => referenceTokens.has(token)).length
    const coverage = matches / referenceTokens.size
    correctnessScore = Math.min(100, Math.max(20, coverage * 120))
  }

  const expectedTime = question?.estimatedTimeSeconds ?? 600
  const efficiencyScore = cleanAnswer
    ? Math.max(35, Math.min(100, (expectedTime / Math.max(timeUsedSeconds, 1)) * 80))
    : 0
  const clarityScore = cleanAnswer
    ? Math.min(100, Math.max(35, cleanAnswer.length / 4))
    : 0
  const logicScore = cleanAnswer
    ? Math.min(100, (correctnessScore * 0.7) + (clarityScore * 0.3))
    : 0

  const finalQuestionScore =
    (correctnessScore * 0.4) +
    (efficiencyScore * 0.25) +
    (clarityScore * 0.2) +
    (logicScore * 0.15)

  const obtainedPoints = Math.round(((question?.basePoints ?? 0) * finalQuestionScore) / 100)
  const feedback = cleanAnswer
    ? "Evaluación automática inicial. Revisa coincidencia conceptual, claridad y tiempo usado."
    : "No se registró una respuesta para esta pregunta."

  return {
    obtainedPoints,
    correctnessScore: Number(correctnessScore.toFixed(1)),
    efficiencyScore: Number(efficiencyScore.toFixed(1)),
    logicScore: Number(logicScore.toFixed(1)),
    clarityScore: Number(clarityScore.toFixed(1)),
    feedback,
  }
}

function average(values: number[]) {
  if (!values.length) return 0
  return values.reduce((sum, value) => sum + value, 0) / values.length
}

export default function InterviewView({ sessionId, onFinish, onCancel }: Props) {
  const { data: allSQ, isLoading: sqLoading } = useSessionQuestions()
  const { data: allQuestions } = useQuestions()

  const updateSQ = useUpdateSessionQuestion()
  const updateSession = useUpdateInterviewSession()

  const [currentIndex, setCurrentIndex] = useState(0)
  const [answers, setAnswers] = useState<Record<string, string>>({})
  const [timeByQuestion, setTimeByQuestion] = useState<Record<string, number>>({})
  const [elapsed, setElapsed] = useState(0)
  const [enteredAt, setEnteredAt] = useState(0)

  const questions = useMemo(
    () => (allSQ ?? [])
      .filter((sq) => sq.sessionId === sessionId)
      .sort((a, b) => a.questionOrder - b.questionOrder),
    [allSQ, sessionId],
  )

  const questionById = useMemo(
    () => new Map((allQuestions ?? []).map((q) => [q.id, q])),
    [allQuestions],
  )

  const currentSQ = questions[currentIndex]
  const questionData = currentSQ?.questionId ? questionById.get(currentSQ.questionId) : undefined

  useEffect(() => {
    const interval = setInterval(() => setElapsed((e) => e + 1), 1000)
    return () => clearInterval(interval)
  }, [])

  const registerTimeForCurrent = useCallback(() => {
    const sq = questions[currentIndex]
    if (!sq) return 0

    const spent = Math.max(0, elapsed - enteredAt)
    setTimeByQuestion((prev) => ({
      ...prev,
      [sq.id]: (prev[sq.id] ?? sq.timeUsedSeconds ?? 0) + spent,
    }))
    setEnteredAt(elapsed)
    return spent
  }, [currentIndex, elapsed, enteredAt, questions])

  const saveAnswer = useCallback(async (index: number, extraTime = 0) => {
    const sq = questions[index]
    if (!sq) return

    const answer = answers[sq.id] ?? ""
    const timeUsedSeconds = (timeByQuestion[sq.id] ?? sq.timeUsedSeconds ?? 0) + extraTime

    if (answer !== sq.userAnswer || timeUsedSeconds !== (sq.timeUsedSeconds ?? 0)) {
      await updateSQ.mutateAsync({
        id: sq.id,
        dto: {
          userAnswer: answer,
          timeUsedSeconds,
          answeredAt: new Date().toISOString(),
        },
      })
    }
  }, [answers, questions, timeByQuestion, updateSQ])

  const handleNext = async () => {
    const spent = registerTimeForCurrent()
    await saveAnswer(currentIndex, spent)
    if (currentIndex < questions.length - 1) {
      setCurrentIndex((i) => i + 1)
      setEnteredAt(elapsed)
    }
  }

  const handlePrev = () => {
    registerTimeForCurrent()
    if (currentIndex > 0) {
      setCurrentIndex((i) => i - 1)
      setEnteredAt(elapsed)
    }
  }

  const handleFinish = async () => {
    const spent = registerTimeForCurrent()
    await saveAnswer(currentIndex, spent)

    const evaluations: Evaluation[] = []

    for (const sq of questions) {
      const answer = answers[sq.id] ?? sq.userAnswer ?? ""
      const timeUsedSeconds = (timeByQuestion[sq.id] ?? sq.timeUsedSeconds ?? 0) + (sq.id === currentSQ?.id ? spent : 0)
      const question = sq.questionId ? questionById.get(sq.questionId) : undefined
      const evaluation = evaluateAnswer(question, answer, timeUsedSeconds)
      evaluations.push(evaluation)

      await updateSQ.mutateAsync({
        id: sq.id,
        dto: {
          userAnswer: answer,
          timeUsedSeconds,
          answeredAt: new Date().toISOString(),
          obtainedPoints: evaluation.obtainedPoints,
          correctnessScore: evaluation.correctnessScore,
          efficiencyScore: evaluation.efficiencyScore,
          logicScore: evaluation.logicScore,
          clarityScore: evaluation.clarityScore,
          evaluationFeedback: evaluation.feedback,
        },
      })
    }

    const correctnessScore = average(evaluations.map((e) => e.correctnessScore))
    const efficiencyScore = average(evaluations.map((e) => e.efficiencyScore))
    const logicScore = average(evaluations.map((e) => e.logicScore))
    const clarityScore = average(evaluations.map((e) => e.clarityScore))
    const finalScore =
      (correctnessScore * 0.4) +
      (efficiencyScore * 0.25) +
      (clarityScore * 0.2) +
      (logicScore * 0.15)

    await updateSession.mutateAsync({
      id: sessionId,
      dto: {
        status: "COMPLETED",
        finishedAt: new Date().toISOString(),
        totalTimeUsedSeconds: elapsed,
        finalScore: Number(finalScore.toFixed(1)),
        correctnessScore: Number(correctnessScore.toFixed(1)),
        efficiencyScore: Number(efficiencyScore.toFixed(1)),
        logicScore: Number(logicScore.toFixed(1)),
        clarityScore: Number(clarityScore.toFixed(1)),
      },
    })
    onFinish(sessionId)
  }

  if (sqLoading) return <p className="text-muted-foreground">Cargando entrevista...</p>

  if (questions.length === 0) {
    return <p className="text-muted-foreground">No hay preguntas en esta sesión.</p>
  }

  const fmt = (s: number) => {
    const m = Math.floor(s / 60)
    const sec = s % 60
    return `${m}:${sec.toString().padStart(2, "0")}`
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-bold">Entrevista</h2>
          <p className="text-sm text-muted-foreground">
            Pregunta {currentIndex + 1} de {questions.length}
          </p>
        </div>
        <div className="flex items-center gap-4">
          <span className="font-mono text-sm tabular-nums">{fmt(elapsed)}</span>
          <button onClick={onCancel} className="text-sm text-muted-foreground transition-colors hover:text-foreground">
            Abandonar
          </button>
        </div>
      </div>

      <div className="flex gap-1">
        {questions.map((_, i) => (
          <div
            key={i}
            className={`h-1 flex-1 rounded-full transition-colors ${
              i < currentIndex ? "bg-emerald-500" : i === currentIndex ? "bg-neutral-900" : "bg-neutral-200"
            }`}
          />
        ))}
      </div>

      {questionData && (
        <Card>
          <CardContent className="space-y-4 pt-6">
            <div className="flex flex-wrap items-center gap-2 text-xs text-muted-foreground">
              <span className="rounded-md bg-neutral-100 px-2 py-1">{questionData.questionType}</span>
              <span className="rounded-md bg-neutral-100 px-2 py-1">{questionData.answerFormat}</span>
              <span className="rounded-md bg-neutral-100 px-2 py-1">{questionData.basePoints} pts</span>
            </div>
            <p className="font-medium">{questionData.statement}</p>
          </CardContent>
        </Card>
      )}

      <div>
        <label className="mb-2 block text-sm font-medium text-foreground">Tu respuesta</label>
        <textarea
          value={currentSQ ? (answers[currentSQ.id] ?? currentSQ.userAnswer ?? "") : ""}
          onChange={(e) => {
            if (currentSQ) {
              setAnswers((prev) => ({ ...prev, [currentSQ.id]: e.target.value }))
            }
          }}
          placeholder="Escribe tu respuesta aquí..."
          rows={8}
          className="w-full resize-y rounded-lg border border-border bg-white p-4 font-mono text-sm text-foreground transition-all placeholder:text-muted-foreground focus:border-transparent focus:outline-none focus:ring-2 focus:ring-primary"
        />
      </div>

      <div className="flex items-center justify-between">
        <Button variant="outline" onClick={handlePrev} disabled={currentIndex === 0}>
          Anterior
        </Button>
        {currentIndex < questions.length - 1 ? (
          <Button onClick={handleNext} disabled={updateSQ.isPending}>
            Siguiente
          </Button>
        ) : (
          <Button onClick={handleFinish} disabled={updateSession.isPending || updateSQ.isPending}>
            {updateSession.isPending || updateSQ.isPending ? "Finalizando..." : "Finalizar entrevista"}
          </Button>
        )}
      </div>
    </div>
  )
}
