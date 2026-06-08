import { useMemo, useState } from "react"
import { BarChart3, Clock, PlayCircle, Plus, Target, Trophy } from "lucide-react"
import MainLayout from "@/layouts/MainLayout"
import SidebarMenu, { type StudentMenuItem } from "@/components/SidebarMenu"
import CreateSession from "@/pages/student/CreateSession"
import Interview from "@/pages/student/Interview"
import SessionResults from "@/pages/student/SessionResults"
import MySessions from "@/pages/student/MySessions"
import Progress from "@/pages/student/Progress"
import Ranking from "@/pages/student/Ranking"
import ProfileEdit from "@/pages/student/ProfileEdit"
import type { Achievement } from "@/api/achievements"
import { useInterviewSessions } from "@/api/interview-sessions.queries"
import { useCategories } from "@/api/categories.queries"
import { useDifficultyLevels } from "@/api/difficulty-levels.queries"
import { useInterviewTypes } from "@/api/interview-types.queries"
import { Button } from "@/components/ui/button"

type StudentView = "home" | "create" | "interview" | "results" | "sessions" | "progress" | "ranking" | "profile"

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

function statusLabel(status: string) {
  const labels: Record<string, string> = {
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
  const [unlockedAchievements, setUnlockedAchievements] = useState<Achievement[]>([])

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

  const showResults = (sessionId: string, achievements?: Achievement[]) => {
    setActiveSessionId(sessionId)
    setUnlockedAchievements(achievements ?? [])
    setView("results")
    setActiveMenuItem("sessions")
  }

  const goHome = () => {
    setActiveSessionId(null)
    setUnlockedAchievements([])
    setView("home")
    setActiveMenuItem("home")
  }

  const handleMenuSelect = (item: StudentMenuItem) => {
    setActiveMenuItem(item)
    setActiveSessionId(null)
    setView(item === "create" ? "create" : item === "sessions" ? "sessions" : item === "progress" ? "progress" : item === "ranking" ? "ranking" : item === "profile" ? "profile" : "home")
  }

  const content = (() => {
    if (view === "create") {
      return (
        <CreateSession
          onSessionCreated={startInterview}
          onCancel={goHome}
        />
      )
    }

    if (view === "interview" && activeSessionId) {
      return (
        <Interview
          sessionId={activeSessionId}
          onFinish={showResults}
          onCancel={goHome}
        />
      )
    }

    if (view === "results" && activeSessionId) {
      return (
        <SessionResults
          sessionId={activeSessionId}
          unlockedAchievements={unlockedAchievements}
          onBack={goHome}
        />
      )
    }

    if (view === "sessions") {
      return (
        <MySessions
          onContinue={startInterview}
          onResults={showResults}
          onBack={goHome}
        />
      )
    }

    if (view === "progress") {
      return <Progress onBack={goHome} />
    }

    if (view === "ranking") {
      return <Ranking onBack={goHome} />
    }

    if (view === "profile") {
      return <ProfileEdit onBack={goHome} />
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
          <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-white/50">Sesiones</p>
                <p className="mt-1 text-3xl font-bold text-white">{sortedSessions.length}</p>
              </div>
              <PlayCircle className="h-8 w-8 text-blue-400" />
            </div>
          </div>
          <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-white/50">Completadas</p>
                <p className="mt-1 text-3xl font-bold text-white">{completed.length}</p>
              </div>
              <Trophy className="h-8 w-8 text-emerald-400" />
            </div>
          </div>
          <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-white/50">En progreso</p>
                <p className="mt-1 text-3xl font-bold text-white">{inProgress.length}</p>
              </div>
              <Clock className="h-8 w-8 text-amber-400" />
            </div>
          </div>
          <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-white/50">Promedio</p>
                <p className="mt-1 text-3xl font-bold text-white">{averageScore.toFixed(1)}</p>
              </div>
              <BarChart3 className="h-8 w-8 text-violet-400" />
            </div>
          </div>
        </div>

        <div className="grid gap-6 xl:grid-cols-[1fr_360px]">
          <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
            <div className="mb-5 flex items-center justify-between">
              <div>
                <h2 className="font-['Work_Sans'] text-lg font-semibold text-white">Sesiones recientes</h2>
                <p className="text-sm text-white/60">Retoma una entrevista o revisa resultados.</p>
              </div>
            </div>

            {sessionsLoading ? (
              <p className="text-sm text-white/50">Cargando sesiones...</p>
            ) : sortedSessions.length === 0 ? (
              <div className="rounded-lg border border-dashed border-white/20 p-8 text-center">
                <Target className="mx-auto mb-3 h-9 w-9 text-white/30" />
                <p className="font-medium text-white/70">Aún no tienes sesiones</p>
                <p className="mt-1 text-sm text-white/50">Crea tu primer simulacro para empezar a medir tu nivel.</p>
                <Button onClick={() => handleMenuSelect("create")} className="mt-5 gap-2">
                  <Plus className="h-4 w-4" />
                  Crear entrevista
                </Button>
              </div>
            ) : (
              <div className="space-y-3">
                {sortedSessions.slice(0, 4).map((session) => (
                  <div
                    key={session.id}
                    className="flex flex-col gap-3 rounded-lg border border-white/10 bg-white/5 p-4 sm:flex-row sm:items-center sm:justify-between"
                  >
                    <div className="min-w-0">
                      <div className="flex flex-wrap items-center gap-2">
                        <p className="font-medium text-white">{typeName(session.interviewTypeId)}</p>
                        <span className="rounded-md bg-white/10 px-2 py-1 text-xs font-medium text-white/70">
                          {statusLabel(session.status)}
                        </span>
                      </div>
                      <p className="mt-1 text-sm text-white/60">
                        {categoryName(session.categoryId)} · {difficultyName(session.difficultyId)} · {formatDate(session.createdAt)}
                      </p>
                      <p className="mt-1 text-xs text-white/50">
                        Tiempo: {formatDuration(session.totalTimeUsedSeconds)} · Puntaje: {(session.finalScore ?? 0).toFixed(1)}
                      </p>
                    </div>
                    <div className="flex gap-2">
                      {session.status === "IN_PROGRESS" ? (
                        <Button onClick={() => startInterview(session.id)} size="sm">Continuar</Button>
                      ) : (
                        <Button onClick={() => showResults(session.id)} size="sm" variant="outline" className="border-white/30 bg-transparent text-white/70 hover:bg-white/10 hover:text-white">
                          Resultados
                        </Button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
            <h2 className="font-['Work_Sans'] text-lg font-semibold text-white">Ruta sugerida</h2>
            <div className="mt-4 space-y-4">
              {[
                ["1", "Elige una categoría", "Empieza con el área que quieres reforzar."],
                ["2", "Responde sin ayuda", "El simulacro mide tu desempeño real."],
                ["3", "Revisa resultados", "Compara tus puntajes por dimensión."],
                ["4", "Repite con más dificultad", "Sube el nivel cuando tu promedio mejore."],
              ].map(([step, title, description]) => (
                <div key={step} className="flex gap-3">
                  <div className="flex h-7 w-7 flex-shrink-0 items-center justify-center rounded-full bg-white/10 text-xs font-bold text-white/70">
                    {step}
                  </div>
                  <div>
                    <p className="text-sm font-medium text-white">{title}</p>
                    <p className="text-xs text-white/50">{description}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
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
