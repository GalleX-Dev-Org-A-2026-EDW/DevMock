import { useMemo, useState } from "react"
import { BarChart3, Clock, PlayCircle, Plus, Target, Trophy } from "lucide-react"
import MainLayout from "@/layouts/MainLayout"
import SidebarMenu, { type StudentMenuItem } from "@/components/SidebarMenu"
import CreateSessionView from "@/components/CreateSessionView"
import InterviewView from "@/components/InterviewView"
import SessionResultsView from "@/components/SessionResultsView"
import MySessionsView from "@/components/MySessionsView"
import { useInterviewSessions } from "@/api/interview-sessions.queries"
import { useCategories } from "@/api/categories.queries"
import { useDifficultyLevels } from "@/api/difficulty-levels.queries"
import { useInterviewTypes } from "@/api/interview-types.queries"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import type { InterviewSession } from "@/api/interview-sessions"

type StudentView = "home" | "create" | "interview" | "results" | "sessions"

function formatDate(value: string) {
  return new Intl.DateTimeFormat("es-CO", {
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value))
}

function formatDuration(seconds: number | null) {
  if (seconds == null) return "Sin tiempo"
  const minutes = Math.floor(seconds / 60)
  const rest = seconds % 60
  return `${minutes}:${rest.toString().padStart(2, "0")}`
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

export default function Dashboard() {
  const [view, setView] = useState<StudentView>("home")
  const [activeMenuItem, setActiveMenuItem] = useState<StudentMenuItem>("home")
  const [activeSessionId, setActiveSessionId] = useState<string | null>(null)

  const { data: sessions, isLoading: sessionsLoading } = useInterviewSessions()
  const { data: categories } = useCategories(true)
  const { data: difficulties } = useDifficultyLevels()
  const { data: types } = useInterviewTypes(true)

  const sortedSessions = useMemo(
    () => [...(sessions ?? [])].sort((a, b) => Date.parse(b.createdAt) - Date.parse(a.createdAt)),
    [sessions],
  )

  const completed = sortedSessions.filter((s) => s.status === "COMPLETED")
  const inProgress = sortedSessions.filter((s) => s.status === "IN_PROGRESS")
  const averageScore = completed.length
    ? completed.reduce((sum, s) => sum + (s.finalScore ?? 0), 0) / completed.length
    : 0

  const categoryName = (id: string | null) =>
    categories?.find((c) => c.id === id)?.name ?? "Categoría"
  const difficultyName = (id: string | null) =>
    difficulties?.find((d) => d.id === id)?.name ?? "Dificultad"
  const typeName = (id: string | null) =>
    types?.find((t) => t.id === id)?.name ?? "Entrevista"

  const startInterview = (sessionId: string) => {
    setActiveSessionId(sessionId)
    setView("interview")
    setActiveMenuItem("sessions")
  }

  const showResults = (sessionId: string) => {
    setActiveSessionId(sessionId)
    setView("results")
    setActiveMenuItem("sessions")
  }

  const goHome = () => {
    setActiveSessionId(null)
    setView("home")
    setActiveMenuItem("home")
  }

  const handleMenuSelect = (item: StudentMenuItem) => {
    setActiveMenuItem(item)
    setActiveSessionId(null)
    setView(item === "create" ? "create" : item === "sessions" ? "sessions" : "home")
  }

  const content = (() => {
    if (view === "create") {
      return (
        <CreateSessionView
          onSessionCreated={startInterview}
          onCancel={goHome}
        />
      )
    }

    if (view === "interview" && activeSessionId) {
      return (
        <InterviewView
          sessionId={activeSessionId}
          onFinish={showResults}
          onCancel={goHome}
        />
      )
    }

    if (view === "results" && activeSessionId) {
      return (
        <SessionResultsView
          sessionId={activeSessionId}
          onBack={goHome}
        />
      )
    }

    if (view === "sessions") {
      return (
        <MySessionsView
          onContinue={startInterview}
          onResults={showResults}
          onBack={goHome}
        />
      )
    }

    return (
      <div className="space-y-6">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>
            <h1 className="font-['Work_Sans'] text-3xl font-bold text-white">Entrenamiento</h1>
            <p className="mt-1 text-sm text-white/60">
              Practica entrevistas técnicas, revisa tu progreso y mejora por sesión.
            </p>
          </div>
          <Button onClick={() => handleMenuSelect("create")} className="w-full gap-2 lg:w-auto">
            <Plus className="h-4 w-4" />
            Nueva entrevista
          </Button>
        </div>

        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          <Card>
            <CardContent className="pt-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-muted-foreground">Sesiones</p>
                  <p className="mt-1 text-3xl font-bold">{sortedSessions.length}</p>
                </div>
                <PlayCircle className="h-8 w-8 text-blue-500" />
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="pt-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-muted-foreground">Completadas</p>
                  <p className="mt-1 text-3xl font-bold">{completed.length}</p>
                </div>
                <Trophy className="h-8 w-8 text-emerald-500" />
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="pt-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-muted-foreground">En progreso</p>
                  <p className="mt-1 text-3xl font-bold">{inProgress.length}</p>
                </div>
                <Clock className="h-8 w-8 text-amber-500" />
              </div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="pt-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-muted-foreground">Promedio</p>
                  <p className="mt-1 text-3xl font-bold">{averageScore.toFixed(1)}</p>
                </div>
                <BarChart3 className="h-8 w-8 text-violet-500" />
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="grid gap-6 xl:grid-cols-[1fr_360px]">
          <Card>
            <CardContent className="pt-6">
              <div className="mb-5 flex items-center justify-between">
                <div>
                  <h2 className="font-['Work_Sans'] text-lg font-semibold">Sesiones recientes</h2>
                  <p className="text-sm text-muted-foreground">Retoma una entrevista o revisa resultados.</p>
                </div>
              </div>

              {sessionsLoading ? (
                <p className="text-sm text-muted-foreground">Cargando sesiones...</p>
              ) : sortedSessions.length === 0 ? (
                <div className="rounded-lg border border-dashed border-border p-8 text-center">
                  <Target className="mx-auto mb-3 h-9 w-9 text-muted-foreground" />
                  <p className="font-medium">Aún no tienes sesiones</p>
                  <p className="mt-1 text-sm text-muted-foreground">Crea tu primer simulacro para empezar a medir tu nivel.</p>
                  <Button onClick={() => handleMenuSelect("create")} className="mt-5 gap-2">
                    <Plus className="h-4 w-4" />
                    Crear entrevista
                  </Button>
                </div>
              ) : (
                <div className="space-y-3">
                  {sortedSessions.slice(0, 6).map((session) => (
                    <div
                      key={session.id}
                      className="flex flex-col gap-3 rounded-lg border border-border p-4 sm:flex-row sm:items-center sm:justify-between"
                    >
                      <div className="min-w-0">
                        <div className="flex flex-wrap items-center gap-2">
                          <p className="font-medium">{typeName(session.interviewTypeId)}</p>
                          <span className="rounded-md bg-neutral-100 px-2 py-1 text-xs font-medium">
                            {statusLabel(session.status)}
                          </span>
                        </div>
                        <p className="mt-1 text-sm text-muted-foreground">
                          {categoryName(session.categoryId)} · {difficultyName(session.difficultyId)} · {formatDate(session.createdAt)}
                        </p>
                        <p className="mt-1 text-xs text-muted-foreground">
                          Tiempo: {formatDuration(session.totalTimeUsedSeconds)} · Puntaje: {(session.finalScore ?? 0).toFixed(1)}
                        </p>
                      </div>
                      <div className="flex gap-2">
                        {session.status === "IN_PROGRESS" ? (
                          <Button onClick={() => startInterview(session.id)} size="sm">Continuar</Button>
                        ) : (
                          <Button onClick={() => showResults(session.id)} size="sm" variant="outline">Resultados</Button>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardContent className="pt-6">
              <h2 className="font-['Work_Sans'] text-lg font-semibold">Ruta sugerida</h2>
              <div className="mt-4 space-y-4">
                {[
                  ["1", "Elige una categoría", "Empieza con el área que quieres reforzar."],
                  ["2", "Responde sin ayuda", "El simulacro mide tu desempeño real."],
                  ["3", "Revisa resultados", "Compara tus puntajes por dimensión."],
                  ["4", "Repite con más dificultad", "Sube el nivel cuando tu promedio mejore."],
                ].map(([step, title, description]) => (
                  <div key={step} className="flex gap-3">
                    <div className="flex h-7 w-7 flex-shrink-0 items-center justify-center rounded-full bg-neutral-900 text-xs font-bold text-white">
                      {step}
                    </div>
                    <div>
                      <p className="text-sm font-medium">{title}</p>
                      <p className="text-xs text-muted-foreground">{description}</p>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    )
  })()

  return (
    <MainLayout
      sidebar={
        <SidebarMenu
          activeItem={activeMenuItem}
          totalSessions={sortedSessions.length}
          completedSessions={completed.length}
          averageScore={averageScore}
          onSelect={handleMenuSelect}
        />
      }
      content={content}
    />
  )
}
