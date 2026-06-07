import { useInterviewSession } from "@/api/interview-sessions.queries"
import { useSessionQuestions } from "@/api/session-questions.queries"
import { useQuestions } from "@/api/questions.queries"
import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"

type Props = {
  sessionId: string
  onBack: () => void
}

function ScoreBadge({ label, value }: { label: string; value: number | null | undefined }) {
  const numeric = value ?? 0
  const color = numeric >= 80 ? "bg-emerald-50 text-emerald-700" :
    numeric >= 50 ? "bg-amber-50 text-amber-700" :
    "bg-red-50 text-red-700"
  return (
    <div className={`rounded-lg px-4 py-3 text-center ${color}`}>
      <p className="text-2xl font-bold">{numeric.toFixed(1)}</p>
      <p className="mt-0.5 text-xs font-medium">{label}</p>
    </div>
  )
}

function SmallScore({ label, value }: { label: string; value: number | null }) {
  if (value == null) return null
  const color = value >= 80 ? "bg-emerald-50 text-emerald-700" :
    value >= 50 ? "bg-amber-50 text-amber-700" :
    "bg-red-50 text-red-700"

  return (
    <span className={`rounded-md px-2 py-1 text-xs font-medium ${color}`}>
      {label}: {value.toFixed(0)}
    </span>
  )
}

export default function SessionResultsView({ sessionId, onBack }: Props) {
  const { data: session, isLoading: sLoading } = useInterviewSession(sessionId)
  const { data: allSQ, isLoading: sqLoading } = useSessionQuestions()
  const { data: allQuestions } = useQuestions()

  if (sLoading || sqLoading) return <p className="text-muted-foreground">Cargando resultados...</p>

  if (!session) return <p className="text-muted-foreground">Sesión no encontrada.</p>

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
        <div>
          <h2 className="text-xl font-bold">Resultados</h2>
          <p className="text-sm text-muted-foreground">Resumen de tu simulacro y desempeño por pregunta.</p>
        </div>
        <Button variant="outline" onClick={onBack}>Volver al inicio</Button>
      </div>

      <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
        <ScoreBadge label="Final" value={session.finalScore} />
        <ScoreBadge label="Correctness" value={session.correctnessScore} />
        <ScoreBadge label="Efficiency" value={session.efficiencyScore} />
        <ScoreBadge label="Logic" value={session.logicScore} />
        <ScoreBadge label="Clarity" value={session.clarityScore} />
        <div className="rounded-lg bg-neutral-50 px-4 py-3 text-center">
          <p className="text-2xl font-bold">{fmt(session.totalTimeUsedSeconds)}</p>
          <p className="mt-0.5 text-xs font-medium">Tiempo total</p>
        </div>
        <div className="col-span-2 rounded-lg bg-neutral-50 px-4 py-3 text-center">
          <p className="text-sm font-medium capitalize">{session.status.toLowerCase()}</p>
          <p className="mt-0.5 text-xs text-muted-foreground">Estado</p>
        </div>
      </div>

      <div className="space-y-3">
        <h3 className="font-semibold">Preguntas</h3>
        {questions.map((sq, i) => {
          const q = (allQuestions ?? []).find((q) => q.id === sq.questionId)
          return (
            <Card key={sq.id}>
              <CardContent className="pt-6">
                <div className="flex items-start justify-between gap-4">
                  <div className="min-w-0 flex-1">
                    <p className="text-sm font-medium">
                      {i + 1}. {q?.statement ?? "Pregunta"}
                    </p>
                    {sq.userAnswer && (
                      <details className="mt-2">
                        <summary className="cursor-pointer text-xs text-muted-foreground transition-colors hover:text-foreground">
                          Tu respuesta
                        </summary>
                        <p className="mt-1 whitespace-pre-wrap rounded-md bg-neutral-50 p-3 text-sm">{sq.userAnswer}</p>
                      </details>
                    )}
                    {q?.explanation && (
                      <details className="mt-2">
                        <summary className="cursor-pointer text-xs text-muted-foreground transition-colors hover:text-foreground">
                          Explicación sugerida
                        </summary>
                        <p className="mt-1 whitespace-pre-wrap rounded-md bg-neutral-50 p-3 text-sm">{q.explanation}</p>
                      </details>
                    )}
                    {sq.evaluationFeedback && (
                      <p className="mt-2 text-xs text-muted-foreground">{sq.evaluationFeedback}</p>
                    )}
                  </div>
                  <div className="flex max-w-40 flex-shrink-0 flex-wrap justify-end gap-2">
                    {sq.obtainedPoints != null && (
                      <span className="rounded-md bg-neutral-100 px-2 py-1 text-xs font-medium">
                        {sq.obtainedPoints} pts
                      </span>
                    )}
                    <SmallScore label="C" value={sq.correctnessScore} />
                    <SmallScore label="E" value={sq.efficiencyScore} />
                    <SmallScore label="L" value={sq.logicScore} />
                    <SmallScore label="Cl" value={sq.clarityScore} />
                  </div>
                </div>
              </CardContent>
            </Card>
          )
        })}
      </div>
    </div>
  )
}
