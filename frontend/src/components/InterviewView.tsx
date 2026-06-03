import { useState, useEffect, useCallback } from "react"
import { useSessionQuestions } from "@/api/session-questions.queries"
import { useUpdateInterviewSession } from "@/api/interview-sessions.queries"
import { useUpdateSessionQuestion } from "@/api/session-questions.queries"
import { useQuestions } from "@/api/questions.queries"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"

type Props = {
  sessionId: string
  onFinish: (sessionId: string) => void
  onCancel: () => void
}

export default function InterviewView({ sessionId, onFinish, onCancel }: Props) {
  const { data: allSQ, isLoading: sqLoading } = useSessionQuestions()
  const { data: allQuestions } = useQuestions()

  const updateSQ = useUpdateSessionQuestion()
  const updateSession = useUpdateInterviewSession()

  const [currentIndex, setCurrentIndex] = useState(0)
  const [answers, setAnswers] = useState<Record<string, string>>({})
  const [elapsed, setElapsed] = useState(0)

  const questions = (allSQ ?? [])
    .filter((sq) => sq.sessionId === sessionId)
    .sort((a, b) => a.questionOrder - b.questionOrder)

  const currentSQ = questions[currentIndex]
  const questionData = currentSQ
    ? (allQuestions ?? []).find((q) => q.id === currentSQ.questionId)
    : null

  useEffect(() => {
    const interval = setInterval(() => setElapsed((e) => e + 1), 1000)
    return () => clearInterval(interval)
  }, [])

  const saveAnswer = useCallback(async (index: number) => {
    const sq = questions[index]
    if (!sq) return
    const answer = answers[sq.id]
    if (answer !== undefined && answer !== sq.userAnswer) {
      await updateSQ.mutateAsync({
        id: sq.id,
        dto: { userAnswer: answer, timeUsedSeconds: elapsed },
      })
    }
  }, [questions, answers, elapsed, updateSQ])

  const handleNext = async () => {
    await saveAnswer(currentIndex)
    if (currentIndex < questions.length - 1) {
      setCurrentIndex((i) => i + 1)
    }
  }

  const handlePrev = () => {
    if (currentIndex > 0) setCurrentIndex((i) => i - 1)
  }

  const handleFinish = async () => {
    await saveAnswer(currentIndex)
    await updateSession.mutateAsync({
      id: sessionId,
      dto: {
        status: "COMPLETED",
        finishedAt: new Date().toISOString(),
        totalTimeUsedSeconds: elapsed,
      },
    })
    onFinish(sessionId)
  }

  if (sqLoading) return <p className="text-muted-foreground">Cargando entrevista…</p>

  if (questions.length === 0) {
    return <p className="text-muted-foreground">No hay preguntas en esta sesión.</p>
  }

  const fmt = (s: number) => {
    const m = Math.floor(s / 60)
    const sec = s % 60
    return `${m}:${sec.toString().padStart(2, "0")}`
  }

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-bold">Entrevista</h2>
          <p className="text-sm text-muted-foreground">
            Pregunta {currentIndex + 1} de {questions.length}
          </p>
        </div>
        <div className="flex items-center gap-4">
          <span className="text-sm font-mono tabular-nums">{fmt(elapsed)}</span>
          <button onClick={onCancel} className="text-sm text-muted-foreground hover:text-foreground transition-colors">
            Abandonar
          </button>
        </div>
      </div>

      <div className="flex gap-1">
        {questions.map((_, i) => (
          <div
            key={i}
            className={`flex-1 h-1 rounded-full transition-colors ${
              i < currentIndex ? "bg-emerald-500" : i === currentIndex ? "bg-neutral-900" : "bg-neutral-200"
            }`}
          />
        ))}
      </div>

      {questionData && (
        <Card>
          <CardContent className="pt-6 space-y-4">
            <p className="font-medium">{questionData.statement}</p>
            {questionData.expectedAnswer && (
              <details className="text-sm text-muted-foreground">
                <summary className="cursor-pointer hover:text-foreground transition-colors">Ver respuesta esperada</summary>
                <p className="mt-2 p-3 bg-neutral-50 rounded-md whitespace-pre-wrap">{questionData.expectedAnswer}</p>
              </details>
            )}
          </CardContent>
        </Card>
      )}

      <div>
        <label className="block text-sm font-medium text-foreground mb-2">Tu respuesta</label>
        <textarea
          value={currentSQ ? (answers[currentSQ.id] ?? "") : ""}
          onChange={(e) => {
            if (currentSQ) {
              setAnswers((prev) => ({ ...prev, [currentSQ.id]: e.target.value }))
            }
          }}
          placeholder="Escribe tu respuesta aquí..."
          rows={8}
          className="w-full rounded-lg border border-border bg-white p-4 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent resize-y transition-all font-mono"
        />
      </div>

      <div className="flex items-center justify-between">
        <Button variant="outline" onClick={handlePrev} disabled={currentIndex === 0}>
          ← Anterior
        </Button>
        {currentIndex < questions.length - 1 ? (
          <Button onClick={handleNext}>
            Siguiente →
          </Button>
        ) : (
          <Button onClick={handleFinish} disabled={updateSession.isPending}>
            {updateSession.isPending ? "Finalizando..." : "Finalizar entrevista"}
          </Button>
        )}
      </div>
    </div>
  )
}
