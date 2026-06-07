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
      <p className="text-xs font-medium mt-0.5">{label}</p>
    </div>
  )
}

export default function SessionResultsView({ sessionId, onBack }: Props) {
  const { data: session, isLoading: sLoading } = useInterviewSession(sessionId)
  const { data: allSQ, isLoading: sqLoading } = useSessionQuestions()
  const { data: allQuestions } = useQuestions()

  if (sLoading || sqLoading) return <p className="text-muted-foreground">Cargando resultados…</p>

  if (!session) return <p className="text-muted-foreground">Sesión no encontrada.</p>

  const questions = (allSQ ?? [])
    .filter((sq) => sq.sessionId === sessionId)
    .sort((a, b) => a.questionOrder - b.questionOrder)

  const fmt = (s: number | null | undefined) => {
    if (s == null) return "—"
    const m = Math.floor(s / 60)
    const sec = s % 60
    return `${m}:${sec.toString().padStart(2, "0")}`
  }

  return (
    <div className="max-w-3xl mx-auto space-y-8">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-bold">Resultados</h2>
        <Button variant="outline" onClick={onBack}>Volver al inicio</Button>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        <ScoreBadge label="Final" value={session.finalScore} />
        <ScoreBadge label="Correctness" value={session.correctnessScore} />
        <ScoreBadge label="Efficiency" value={session.efficiencyScore} />
        <ScoreBadge label="Logic" value={session.logicScore} />
        <ScoreBadge label="Clarity" value={session.clarityScore} />
        <div className="rounded-lg bg-neutral-50 px-4 py-3 text-center">
          <p className="text-2xl font-bold">{fmt(session.totalTimeUsedSeconds)}</p>
          <p className="text-xs font-medium mt-0.5">Tiempo total</p>
        </div>
        <div className="rounded-lg bg-neutral-50 px-4 py-3 text-center col-span-2">
          <p className="text-sm font-medium capitalize">{session.status.toLowerCase()}</p>
          <p className="text-xs text-muted-foreground mt-0.5">Estado</p>
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
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium">
                      {i + 1}. {q?.statement ?? "Pregunta"}
                    </p>
                    {sq.userAnswer && (
                      <details className="mt-2">
                        <summary className="text-xs text-muted-foreground cursor-pointer hover:text-foreground transition-colors">
                          Tu respuesta
                        </summary>
                        <p className="mt-1 text-sm whitespace-pre-wrap bg-neutral-50 rounded-md p-3">{sq.userAnswer}</p>
                      </details>
                    )}
                    {sq.evaluationFeedback && (
                      <p className="text-xs text-muted-foreground mt-2">{sq.evaluationFeedback}</p>
                    )}
                  </div>
                  <div className="flex gap-2 flex-shrink-0">
                    {sq.obtainedPoints != null && (
                      <span className="text-xs font-medium px-2 py-1 rounded-md bg-neutral-100">
                        {sq.obtainedPoints} pts
                      </span>
                    )}
                    {sq.correctnessScore != null && (
                      <span className={`text-xs font-medium px-2 py-1 rounded-md ${
                        sq.correctnessScore >= 80 ? "bg-emerald-50 text-emerald-700" :
                        sq.correctnessScore >= 50 ? "bg-amber-50 text-amber-700" :
                        "bg-red-50 text-red-700"
                      }`}>
                        C: {sq.correctnessScore.toFixed(0)}
                      </span>
                    )}
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
