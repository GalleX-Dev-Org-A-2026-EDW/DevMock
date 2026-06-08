import { useCallback, useEffect, useMemo, useState } from "react"
import { useSessionQuestions, useUpdateSessionQuestion } from "@/api/session-questions.queries"
import { useUpdateInterviewSession } from "@/api/interview-sessions.queries"
import { useQuestions } from "@/api/questions.queries"
import { Button } from "@/components/ui/button"
import type { Question } from "@/api/questions"
import { evaluationsApi } from "@/api/evaluations"
import CodeEditor from "@/components/CodeEditor"
import ChoiceQuestion from "@/components/ChoiceQuestion"
import { ArrowLeft, Loader2 } from "lucide-react"

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
  let lowered: string
  try {
    lowered = text.toLowerCase().normalize("NFD")
  } catch {
    lowered = text.toLowerCase()
  }
  return lowered
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

function detectLanguage(evalConfig: string | null): "python" | "java" | "sql" {
  if (!evalConfig) return "python"
  try {
    const parsed = JSON.parse(evalConfig)
    const lang = parsed.language
    if (lang === "java" || lang === "sql") return lang
    return "python"
  } catch {
    return "python"
  }
}

function evaluateChoice(question: Question | undefined, selectedId: string | string[]): Evaluation {
  const options = question?.answerOptions ?? []
  const isCorrect = (opt: { isCorrect?: boolean }) => opt.isCorrect === true

  if (question?.answerFormat === "SINGLE_CHOICE") {
    const selected = options.find((o) => o.id === (selectedId as string))
    const correct = selected ? !!selected.isCorrect : false
    const score = correct ? 100 : 0
    return {
      obtainedPoints: correct ? (question.basePoints ?? 0) : 0,
      correctnessScore: score,
      efficiencyScore: 100,
      logicScore: score,
      clarityScore: 100,
      feedback: correct
        ? "Respuesta correcta."
        : `Incorrecto. ${options.find((o) => o.isCorrect)?.optionText ?? ""}`,
    }
  }

  const selectedArr = selectedId as string[]
  const correctOptions = options.filter(isCorrect)
  let hits = 0
  let misses = 0
  for (const id of selectedArr) {
    if (options.find((o) => o.id === id)?.isCorrect) hits++
    else misses++
  }
  const totalCorrect = correctOptions.length
  const precision = hits + misses > 0 ? hits / (hits + misses) : 0
  const recall = totalCorrect > 0 ? hits / totalCorrect : 0
  const f1 = precision + recall > 0 ? 2 * (precision * recall) / (precision + recall) : 0
  const score = Math.round(f1 * 100)
  const obtainedPoints = Math.round(((question?.basePoints ?? 0) * score) / 100)
  return {
    obtainedPoints,
    correctnessScore: score,
    efficiencyScore: 100,
    logicScore: score,
    clarityScore: 100,
    feedback: score >= 100
      ? "Todas las opciones correctas seleccionadas."
      : score >= 60
        ? "Mayoría de opciones correctas."
        : "Varias opciones incorrectas seleccionadas.",
  }
}

export default function Interview({ sessionId, onFinish, onCancel }: Props) {
  const { data: allSQ, isLoading: sqLoading } = useSessionQuestions()
  const { data: allQuestions } = useQuestions()

  const updateSQ = useUpdateSessionQuestion()
  const updateSession = useUpdateInterviewSession()

  const [currentIndex, setCurrentIndex] = useState(0)
  const [answers, setAnswers] = useState<Record<string, string>>({})
  const [selectedOptions, setSelectedOptions] = useState<Record<string, string | string[]>>({})
  const [timeByQuestion, setTimeByQuestion] = useState<Record<string, number>>({})
  const [elapsed, setElapsed] = useState(0)
  const [enteredAt, setEnteredAt] = useState(0)
  const [isEvaluating, setIsEvaluating] = useState(false)
  const [error, setError] = useState<string | null>(null)

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
    const selectedId = selectedOptions[sq.id]

    const dto: Record<string, unknown> = {
      userAnswer: answer,
      timeUsedSeconds,
      answeredAt: new Date().toISOString(),
    }

    if (typeof selectedId === "string") {
      dto.selectedOptionId = selectedId
      dto.userAnswer = ""
    } else if (Array.isArray(selectedId)) {
      dto.userAnswer = JSON.stringify(selectedId)
    }

    await updateSQ.mutateAsync({ id: sq.id, dto: dto as never })
  }, [answers, questions, selectedOptions, timeByQuestion, updateSQ])

  const handleNext = async () => {
    try {
      const spent = registerTimeForCurrent()
      await saveAnswer(currentIndex, spent)
      if (currentIndex < questions.length - 1) {
        setCurrentIndex((i) => i + 1)
        setEnteredAt(elapsed)
      }
    } catch {
      setError("Error al guardar. Revisa tu respuesta e intenta de nuevo.")
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

    const evaluations: Evaluation[] = []

    setIsEvaluating(true)
    setError(null)
    try {
      for (const sq of questions) {
        const answer = answers[sq.id] ?? sq.userAnswer
        const timeUsedSeconds = (timeByQuestion[sq.id] ?? sq.timeUsedSeconds ?? 0) + (sq.id === currentSQ?.id ? spent : 0)
        const question = sq.questionId ? questionById.get(sq.questionId) : undefined
        if (!question) continue

        let evaluation: Evaluation
        const fmt = question.answerFormat

        if (fmt === "CODE") {
          const result = await evaluationsApi.evaluateCode({
            code: answer ?? "",
            expectedAnswer: question.expectedAnswer ?? null,
            evaluationConfig: question.evaluationConfig ?? null,
            estimatedTimeSeconds: question.estimatedTimeSeconds,
            timeUsedSeconds,
            basePoints: question.basePoints,
          })
          evaluation = {
            obtainedPoints: result.obtainedPoints,
            correctnessScore: result.correctnessScore,
            efficiencyScore: result.efficiencyScore,
            logicScore: result.logicScore,
            clarityScore: result.clarityScore,
            feedback: result.evaluationFeedback,
          }
        } else if (fmt === "SINGLE_CHOICE" || fmt === "MULTIPLE_CHOICE") {
          const selectedId = selectedOptions[sq.id] ?? ""
          evaluation = evaluateChoice(question, selectedId)
        } else {
          evaluation = evaluateAnswer(question, answer ?? "", timeUsedSeconds)
        }
        evaluations.push(evaluation)

        const dto: Record<string, unknown> = {
          timeUsedSeconds,
          answeredAt: new Date().toISOString(),
          obtainedPoints: evaluation.obtainedPoints,
          correctnessScore: evaluation.correctnessScore,
          efficiencyScore: evaluation.efficiencyScore,
          logicScore: evaluation.logicScore,
          clarityScore: evaluation.clarityScore,
          evaluationFeedback: evaluation.feedback,
        }
        if (answer != null) {
          dto.userAnswer = answer
        }

        await updateSQ.mutateAsync({ id: sq.id, dto: dto as never })
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
    } catch {
      setError("Error al finalizar la entrevista. Intenta de nuevo.")
    } finally {
      setIsEvaluating(false)
    }
  }

  if (sqLoading) {
    return (
      <div className="flex items-center justify-center py-32">
        <div className="text-center">
          <div className="mx-auto mb-3 h-8 w-8 animate-spin rounded-full border-2 border-emerald-500 border-t-transparent" />
          <p className="text-sm text-white/60">Cargando entrevista...</p>
        </div>
      </div>
    )
  }

  if (questions.length === 0) {
    return (
      <div className="flex items-center justify-center py-32">
        <p className="text-sm text-white/60">No hay preguntas en esta sesión.</p>
      </div>
    )
  }

  const fmt = (s: number) => {
    const m = Math.floor(s / 60)
    const sec = s % 60
    return `${m}:${sec.toString().padStart(2, "0")}`
  }

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button
            onClick={onCancel}
            className="rounded-lg p-2 text-white/60 transition-colors hover:bg-white/10 hover:text-white"
            aria-label="Abandonar entrevista"
          >
            <ArrowLeft className="h-5 w-5" />
          </button>
          <div>
            <h1 className="font-['Work_Sans'] text-2xl font-bold text-white">Entrevista</h1>
            <p className="mt-1 text-sm text-white/60">
              Pregunta {currentIndex + 1} de {questions.length}
            </p>
          </div>
        </div>
        <div className="flex items-center gap-4">
          <span className="font-mono text-sm tabular-nums text-white/70">{fmt(elapsed)}</span>
          <button
            onClick={onCancel}
            className="text-sm text-white/50 transition-colors hover:text-white"
          >
            Abandonar
          </button>
        </div>
      </div>

      <div className="flex gap-1">
        {questions.map((_, i) => (
          <div
            key={i}
            className={`h-1.5 flex-1 rounded-full transition-colors ${
              i < currentIndex ? "bg-emerald-500" : i === currentIndex ? "bg-emerald-500/60" : "bg-white/10"
            }`}
          />
        ))}
      </div>

      {questionData && (
        <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
          <div className="mb-3 flex flex-wrap items-center gap-2">
            <span className="rounded-md bg-white/10 px-2 py-1 text-xs text-white/70">{questionData.questionType}</span>
            <span className="rounded-md bg-white/10 px-2 py-1 text-xs text-white/70">{questionData.answerFormat}</span>
            <span className="rounded-md bg-white/10 px-2 py-1 text-xs text-white/70">{questionData.basePoints} pts</span>
          </div>
          <p className="text-white">{questionData.statement}</p>
        </div>
      )}

      <div>
        <p className="mb-2 text-sm font-medium text-white/70">
          {questionData?.answerFormat === "CODE"
            ? "Tu código"
            : questionData?.answerFormat === "SINGLE_CHOICE" || questionData?.answerFormat === "MULTIPLE_CHOICE"
              ? "Selecciona una opción"
              : "Tu respuesta"}
        </p>
        {questionData?.answerFormat === "CODE" ? (
          <CodeEditor
            value={currentSQ ? (answers[currentSQ.id] ?? currentSQ.userAnswer ?? "") : ""}
            onChange={(v) => {
              if (currentSQ) setAnswers((prev) => ({ ...prev, [currentSQ.id]: v }))
            }}
            language={detectLanguage(questionData.evaluationConfig)}
          />
        ) : questionData?.answerFormat === "SINGLE_CHOICE" ? (
          <ChoiceQuestion
            type="SINGLE_CHOICE"
            options={questionData.answerOptions}
            selectedId={currentSQ ? (selectedOptions[currentSQ.id] ?? "") : ""}
            onChange={(id) => {
              if (currentSQ) {
                setSelectedOptions((prev) => ({ ...prev, [currentSQ.id]: id as string }))
              }
            }}
          />
        ) : questionData?.answerFormat === "MULTIPLE_CHOICE" ? (
          <ChoiceQuestion
            type="MULTIPLE_CHOICE"
            options={questionData.answerOptions}
            selectedId={currentSQ ? (selectedOptions[currentSQ.id] ?? []) : []}
            onChange={(ids) => {
              if (currentSQ) {
                setSelectedOptions((prev) => ({ ...prev, [currentSQ.id]: ids as string[] }))
              }
            }}
          />
        ) : (
          <textarea
            value={currentSQ ? (answers[currentSQ.id] ?? currentSQ.userAnswer ?? "") : ""}
            onChange={(e) => {
              if (currentSQ) {
                setAnswers((prev) => ({ ...prev, [currentSQ.id]: e.target.value }))
              }
            }}
            placeholder="Escribe tu respuesta aquí..."
            rows={8}
            className="w-full resize-y rounded-lg border border-white/20 bg-white/10 p-4 font-mono text-sm text-white placeholder:text-white/40 transition-all focus:border-white/30 focus:outline-none focus:ring-2 focus:ring-white/30"
          />
        )}
      </div>

      {error && (
        <div className="rounded-lg border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-300">
          {error}
          <button
            onClick={() => setError(null)}
            className="ml-2 underline hover:text-red-200"
          >
            Cerrar
          </button>
        </div>
      )}

      <div className="flex items-center justify-between">
        <Button
          variant="outline"
          onClick={handlePrev}
          disabled={currentIndex === 0}
          className="border-white/30 bg-transparent text-white/70 hover:bg-white/10 hover:text-white"
        >
          Anterior
        </Button>
        {currentIndex < questions.length - 1 ? (
          <Button onClick={handleNext} disabled={updateSQ.isPending}>
            {updateSQ.isPending ? (
              <><Loader2 className="h-4 w-4 animate-spin" /> Guardando...</>
            ) : "Siguiente"}
          </Button>
        ) : (
          <Button onClick={handleFinish} disabled={isEvaluating || updateSession.isPending || updateSQ.isPending}>
            {isEvaluating ? (
              <><Loader2 className="h-4 w-4 animate-spin" /> Enviando...</>
            ) : updateSession.isPending || updateSQ.isPending ? (
              <><Loader2 className="h-4 w-4 animate-spin" /> Finalizando...</>
            ) : "Finalizar entrevista"}
          </Button>
        )}
      </div>

      {isEvaluating && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm">
          <div className="flex flex-col items-center gap-6">
            <div className="relative">
              <div className="h-16 w-16 animate-spin rounded-full border-4 border-emerald-500/30 border-t-emerald-400" />
              <div className="absolute inset-0 h-16 w-16 animate-ping rounded-full border-4 border-emerald-400/20" />
            </div>
            <div className="text-center">
              <p className="font-['Work_Sans'] text-2xl font-semibold text-white">Evaluando respuestas...</p>
              <p className="mt-2 text-sm text-white/50">Procesando y calificando cada pregunta</p>
            </div>
            <div className="flex items-center gap-1.5">
              <span className="h-2 w-2 animate-bounce rounded-full bg-emerald-400 [animation-delay:0ms]" />
              <span className="h-2 w-2 animate-bounce rounded-full bg-emerald-400 [animation-delay:150ms]" />
              <span className="h-2 w-2 animate-bounce rounded-full bg-emerald-400 [animation-delay:300ms]" />
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
