import { useInterviewSession } from "@/api/interview-sessions.queries"
import { useSessionQuestions } from "@/api/session-questions.queries"
import { useQuestions } from "@/api/questions.queries"
import type { Question } from "@/api/questions"
import type { SessionQuestion } from "@/api/session-questions"
import { ArrowLeft } from "lucide-react"

type Props = {
  sessionId: string
  onBack: () => void
}

function ScoreBadge({ label, value }: { label: string; value: number | null | undefined }) {
  const numeric = value ?? 0
  const color =
    numeric >= 80 ? "border-emerald-500/30 bg-emerald-500/10 text-emerald-400" :
    numeric >= 50 ? "border-amber-500/30 bg-amber-500/10 text-amber-400" :
    "border-red-500/30 bg-red-500/10 text-red-400"
  return (
    <div className={`rounded-lg border px-4 py-3 text-center ${color}`}>
      <p className="text-2xl font-bold">{numeric.toFixed(1)}</p>
      <p className="mt-0.5 text-xs font-medium">{label}</p>
    </div>
  )
}

function SmallScore({ label, value }: { label: string; value: number | null }) {
  if (value == null) return null
  const color =
    value >= 80 ? "bg-emerald-500/15 text-emerald-400" :
    value >= 50 ? "bg-amber-500/15 text-amber-400" :
    "bg-red-500/15 text-red-400"

  return (
    <span className={`rounded-md px-2 py-1 text-xs font-medium ${color}`}>
      {label}: {value.toFixed(0)}
    </span>
  )
}

function getDisplayAnswer(
  sq: Pick<SessionQuestion, "userAnswer" | "selectedOptionId">,
  question: Pick<Question, "answerFormat" | "answerOptions"> | undefined,
): string | null {
  if (!question) return sq.userAnswer

  if (question.answerFormat === "SINGLE_CHOICE") {
    if (!sq.selectedOptionId) return null
    const opt = question.answerOptions?.find((o) => o.id === sq.selectedOptionId)
    return opt?.optionText ?? null
  }

  if (question.answerFormat === "MULTIPLE_CHOICE") {
    if (!sq.userAnswer) return null
    try {
      const ids = JSON.parse(sq.userAnswer)
      if (!Array.isArray(ids)) return sq.userAnswer
      return ids
        .map((id: string) => {
          const opt = question.answerOptions?.find((o) => o.id === id)
          return opt?.optionText ?? id
        })
        .join(", ")
    } catch {
      return sq.userAnswer
    }
  }

  return sq.userAnswer
}

export default function SessionResults({ sessionId, onBack }: Props) {
  const { data: session, isLoading: sLoading } = useInterviewSession(sessionId)
  const { data: allSQ, isLoading: sqLoading } = useSessionQuestions()
  const { data: allQuestions } = useQuestions()

  if (sLoading || sqLoading) {
    return (
      <div className="flex items-center justify-center py-32">
        <div className="text-center">
          <div className="mx-auto mb-3 h-8 w-8 animate-spin rounded-full border-2 border-emerald-500 border-t-transparent" />
          <p className="text-sm text-white/60">Cargando resultados...</p>
        </div>
      </div>
    )
  }

  if (!session) {
    return (
      <div className="flex items-center justify-center py-32">
        <p className="text-sm text-white/60">Sesión no encontrada.</p>
      </div>
    )
  }

  const questions = (allSQ ?? [])
    .filter((sq) => sq.sessionId === sessionId)
    .sort((a, b) => a.questionOrder - b.questionOrder)

  const fmt = (s: number | null | undefined) => {
    if (s == null) return "-"
    const m = Math.floor(s / 60)
    const sec = s % 60
    return `${m}:${sec.toString().padStart(2, "0")}`
  }

  return (
    <div className="mx-auto max-w-3xl space-y-8">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button
            onClick={onBack}
            className="rounded-lg p-2 text-white/60 transition-colors hover:bg-white/10 hover:text-white"
            aria-label="Volver al inicio"
          >
            <ArrowLeft className="h-5 w-5" />
          </button>
          <div>
            <h1 className="font-['Work_Sans'] text-2xl font-bold text-white">Resultados</h1>
            <p className="mt-1 text-sm text-white/60">Resumen de tu simulacro y desempeño por pregunta.</p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
        <ScoreBadge label="Final" value={session.finalScore} />
        <ScoreBadge label="Corrección" value={session.correctnessScore} />
        <ScoreBadge label="Eficiencia" value={session.efficiencyScore} />
        <ScoreBadge label="Lógica" value={session.logicScore} />
        <ScoreBadge label="Claridad" value={session.clarityScore} />
        <div className="rounded-lg border border-white/10 bg-white/10 px-4 py-3 text-center">
          <p className="text-2xl font-bold text-white">{fmt(session.totalTimeUsedSeconds)}</p>
          <p className="mt-0.5 text-xs font-medium text-white/50">Tiempo total</p>
        </div>
        <div className="col-span-2 rounded-lg border border-white/10 bg-white/10 px-4 py-3 text-center">
          <p className="text-sm font-medium capitalize text-white">{session.status.toLowerCase()}</p>
          <p className="mt-0.5 text-xs text-white/50">Estado</p>
        </div>
      </div>

      <div className="space-y-3">
        <h2 className="font-['Work_Sans'] text-lg font-semibold text-white">Preguntas</h2>
        {questions.map((sq, i) => {
          const q = (allQuestions ?? []).find((q) => q.id === sq.questionId)
          return (
            <div key={sq.id} className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
              <div className="flex items-start justify-between gap-4">
                <div className="min-w-0 flex-1">
                  <p className="text-sm font-medium text-white">
                    {i + 1}. {q?.statement ?? "Pregunta"}
                  </p>
                  {(() => {
                    const displayAnswer = getDisplayAnswer(sq, q)
                    return displayAnswer ? (
                      <details className="mt-2">
                        <summary className="cursor-pointer text-xs text-white/50 transition-colors hover:text-white">
                          Tu respuesta
                        </summary>
                        <p className="mt-1 whitespace-pre-wrap rounded-md border border-white/10 bg-white/5 p-3 text-sm text-white/80">
                          {displayAnswer}
                        </p>
                      </details>
                    ) : null
                  })()}
                  {q?.explanation && (
                    <details className="mt-2">
                      <summary className="cursor-pointer text-xs text-white/50 transition-colors hover:text-white">
                        Explicación sugerida
                      </summary>
                      <p className="mt-1 whitespace-pre-wrap rounded-md border border-white/10 bg-white/5 p-3 text-sm text-white/80">
                        {q.explanation}
                      </p>
                    </details>
                  )}
                  {sq.evaluationFeedback && (
                    <p className="mt-2 text-xs text-white/50">{sq.evaluationFeedback}</p>
                  )}
                </div>
                <div className="flex max-w-40 flex-shrink-0 flex-wrap justify-end gap-2">
                  {sq.obtainedPoints != null && (
                    <span className="rounded-md bg-white/10 px-2 py-1 text-xs font-medium text-white/70">
                      {sq.obtainedPoints} pts
                    </span>
                  )}
                  <SmallScore label="Co" value={sq.correctnessScore} />
                  <SmallScore label="Ef" value={sq.efficiencyScore} />
                  <SmallScore label="Lg" value={sq.logicScore} />
                  <SmallScore label="Cl" value={sq.clarityScore} />
                </div>
              </div>
            </div>
          )
        })}
      </div>
    </div>
  )
}
