import { useMemo, useState } from "react"
import { useInterviewSessions } from "@/api/interview-sessions.queries"
import { useCategories } from "@/api/categories.queries"
import { useDifficultyLevels } from "@/api/difficulty-levels.queries"
import { useInterviewTypes } from "@/api/interview-types.queries"
import { Button } from "@/components/ui/button"
import { ArrowLeft, BarChart3, ClipboardList, Play } from "lucide-react"
import type { InterviewSession } from "@/api/interview-sessions"

type Props = {
  onContinue: (sessionId: string) => void
  onResults: (sessionId: string) => void
  onBack: () => void
}

type FilterTab = "all" | "in_progress" | "completed"

function formatDate(value: string) {
  return new Intl.DateTimeFormat("es-CO", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value))
}

function formatDuration(seconds: number | null) {
  if (seconds == null) return "—"
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${s.toString().padStart(2, "0")}`
}

function statusLabel(status: InterviewSession["status"]) {
  const labels: Record<InterviewSession["status"], string> = {
    IN_PROGRESS: "En progreso",
    COMPLETED: "Completada",
    ABANDONED: "Abandonada",
    EXPIRED: "Expirada",
  }
  return labels[status] ?? status
}

function statusColor(status: InterviewSession["status"]) {
  const colors: Record<InterviewSession["status"], string> = {
    IN_PROGRESS: "bg-amber-50 text-amber-700",
    COMPLETED: "bg-emerald-50 text-emerald-700",
    ABANDONED: "bg-red-50 text-red-700",
    EXPIRED: "bg-neutral-100 text-neutral-500",
  }
  return colors[status] ?? "bg-neutral-100 text-neutral-500"
}

export default function MySessionsView({ onContinue, onResults, onBack }: Props) {
  const { data: sessions, isLoading } = useInterviewSessions()
  const { data: categories } = useCategories(true)
  const { data: difficulties } = useDifficultyLevels()
  const { data: types } = useInterviewTypes(true)
  const [filter, setFilter] = useState<FilterTab>("all")

  const sortedSessions = useMemo(
    () => [...(sessions ?? [])].sort((a, b) => Date.parse(b.createdAt) - Date.parse(a.createdAt)),
    [sessions],
  )

  const filtered = useMemo(() => {
    if (filter === "all") return sortedSessions
    if (filter === "in_progress") return sortedSessions.filter((s) => s.status === "IN_PROGRESS")
    return sortedSessions.filter((s) => s.status === "COMPLETED")
  }, [sortedSessions, filter])

  const typeName = (id: string | null) => types?.find((t) => t.id === id)?.name ?? "Entrevista"
  const categoryName = (id: string | null) => categories?.find((c) => c.id === id)?.name ?? "Categoría"
  const difficultyName = (id: string | null) => difficulties?.find((d) => d.id === id)?.name ?? "Dificultad"

  const counts = useMemo(() => ({
    all: sortedSessions.length,
    in_progress: sortedSessions.filter((s) => s.status === "IN_PROGRESS").length,
    completed: sortedSessions.filter((s) => s.status === "COMPLETED").length,
  }), [sortedSessions])

  const tabs: Array<{ key: FilterTab; label: string; count: number }> = [
    { key: "all", label: "Todas", count: counts.all },
    { key: "in_progress", label: "En progreso", count: counts.in_progress },
    { key: "completed", label: "Completadas", count: counts.completed },
  ]

  return (
    <div className="space-y-6">
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
            <h1 className="font-['Work_Sans'] text-2xl font-bold text-white">Mis sesiones</h1>
            <p className="mt-1 text-sm text-white/60">
              Historial completo de tus simulacros de entrevista.
            </p>
          </div>
        </div>
      </div>

      <div className="flex gap-1 rounded-lg bg-white/10 p-1">
        {tabs.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setFilter(tab.key)}
            className={`flex-1 rounded-md px-4 py-2 text-sm font-medium transition-all ${
              filter === tab.key
                ? "bg-white text-[#16213e] shadow-sm"
                : "text-white/60 hover:text-white"
            }`}
          >
            {tab.label} ({tab.count})
          </button>
        ))}
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center py-20">
          <div className="text-center">
            <div className="mx-auto mb-3 h-8 w-8 animate-spin rounded-full border-2 border-emerald-500 border-t-transparent" />
            <p className="text-sm text-white/60">Cargando sesiones...</p>
          </div>
        </div>
      ) : filtered.length === 0 ? (
        <div className="rounded-lg border border-dashed border-white/20 p-16 text-center">
          <ClipboardList className="mx-auto mb-4 h-12 w-12 text-white/30" />
          <p className="text-lg font-medium text-white/70">
            {filter === "all"
              ? "Aún no tienes sesiones"
              : filter === "in_progress"
                ? "No tienes sesiones en progreso"
                : "No tienes sesiones completadas"}
          </p>
          <p className="mt-2 text-sm text-white/40">
            {filter === "all"
              ? "Crea tu primer simulacro para empezar a medir tu nivel."
              : "Cambia de filtro o inicia una nueva entrevista desde el inicio."}
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {filtered.map((session) => (
            <div
              key={session.id}
              className="flex flex-col gap-4 rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm transition-all hover:bg-white/15 sm:flex-row sm:items-center sm:justify-between"
            >
              <div className="min-w-0 flex-1">
                <div className="flex flex-wrap items-center gap-2">
                  <p className="font-semibold text-white">
                    {typeName(session.interviewTypeId)}
                  </p>
                  <span className={`rounded-md px-2 py-0.5 text-xs font-medium ${statusColor(session.status)}`}>
                    {statusLabel(session.status)}
                  </span>
                </div>
                <p className="mt-1 text-sm text-white/50">
                  {categoryName(session.categoryId)} · {difficultyName(session.difficultyId)}
                </p>
                <div className="mt-1 flex flex-wrap items-center gap-3 text-xs text-white/40">
                  <span>{formatDate(session.createdAt)}</span>
                  <span>Tiempo: {formatDuration(session.totalTimeUsedSeconds)}</span>
                  {session.finalScore != null && (
                    <span>Puntaje: {session.finalScore.toFixed(1)}</span>
                  )}
                </div>
              </div>
              <div className="flex flex-shrink-0 gap-2">
                {session.status === "IN_PROGRESS" ? (
                  <Button
                    onClick={() => onContinue(session.id)}
                    size="sm"
                    className="gap-1.5 bg-emerald-500 text-white hover:bg-emerald-600"
                  >
                    <Play className="h-3.5 w-3.5" />
                    Continuar
                  </Button>
                ) : session.status === "COMPLETED" ? (
                  <Button
                    onClick={() => onResults(session.id)}
                    size="sm"
                    variant="outline"
                    className="gap-1.5 border-white/20 bg-transparent text-white/80 hover:bg-white/20 hover:text-white"
                  >
                    <BarChart3 className="h-3.5 w-3.5" />
                    Resultados
                  </Button>
                ) : null}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
