import { useMemo, useState } from "react"
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Filler,
} from "chart.js"
import { Line, Bar } from "react-chartjs-2"
import { useInterviewSessions } from "@/api/interview-sessions.queries"
import { useCategories } from "@/api/categories.queries"
import { useAchievements } from "@/api/achievements.queries"
import { useUserAchievements } from "@/api/user-achievements.queries"
import { ArrowLeft, BarChart3, Clock, PlayCircle, Trophy } from "lucide-react"

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Filler,
)

type Props = {
  onBack: () => void
}

function formatDuration(totalSeconds: number) {
  const hours = Math.floor(totalSeconds / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  if (hours > 0) return `${hours}h ${minutes}m`
  return `${minutes}m`
}

function formatShortDate(value: string) {
  return new Intl.DateTimeFormat("es-CO", {
    day: "2-digit",
    month: "short",
  }).format(new Date(value))
}

export default function Progress({ onBack }: Props) {
  const { data: sessions, isLoading } = useInterviewSessions()
  const { data: categories } = useCategories(true)
  const { data: allAchievements } = useAchievements()
  const { data: userAchievements } = useUserAchievements()
  const [chartView, setChartView] = useState<"score" | "dimensions">("score")

  const sortedCompleted = useMemo(() => {
    if (!sessions) return []
    return [...sessions]
      .filter((s) => s.status === "COMPLETED")
      .sort((a, b) => Date.parse(a.createdAt) - Date.parse(b.createdAt))
  }, [sessions])

  const completed = useMemo(
    () => (sessions ?? []).filter((s) => s.status === "COMPLETED"),
    [sessions],
  )

  const totalTime = useMemo(
    () => completed.reduce((sum, s) => sum + (s.totalTimeUsedSeconds ?? 0), 0),
    [completed],
  )

  const averageScore = completed.length
    ? completed.reduce((sum, s) => sum + (s.finalScore ?? 0), 0) / completed.length
    : 0

  const avgDimension = (field: (s: typeof completed[number]) => number | null) => {
    const vals = completed.map(field).filter((v): v is number => v != null)
    return vals.length ? vals.reduce((a, b) => a + b, 0) / vals.length : 0
  }

  const categoryName = (id: string | null) =>
    categories?.find((c) => c.id === id)?.name ?? "Otra"

  const categoryPerformance = useMemo(() => {
    const grouped: Record<string, number[]> = {}
    for (const s of completed) {
      const key = s.categoryId ?? "unknown"
      if (!grouped[key]) grouped[key] = []
      if (s.finalScore != null) grouped[key].push(s.finalScore)
    }
    return Object.entries(grouped)
      .map(([id, scores]) => ({
        id,
        name: categoryName(id),
        avg: scores.reduce((a, b) => a + b, 0) / scores.length,
        count: scores.length,
      }))
      .sort((a, b) => b.avg - a.avg)
  }, [completed, categories])

  const scoreChartData = useMemo(() => ({
    labels: sortedCompleted.map((s) => formatShortDate(s.createdAt)),
    datasets: [
      {
        label: "Puntaje final",
        data: sortedCompleted.map((s) => s.finalScore ?? 0),
        borderColor: "#10b981",
        backgroundColor: "rgba(16, 185, 129, 0.1)",
        fill: true,
        tension: 0.3,
        pointRadius: 4,
        pointBackgroundColor: "#10b981",
        pointBorderColor: "#10b981",
      },
    ],
  }), [sortedCompleted])

  const categoryChartData = useMemo(() => ({
    labels: categoryPerformance.map((c) => c.name),
    datasets: [
      {
        label: "Puntaje promedio",
        data: categoryPerformance.map((c) => Number(c.avg.toFixed(1))),
        backgroundColor: "rgba(16, 185, 129, 0.7)",
        borderColor: "#10b981",
        borderWidth: 1,
        borderRadius: 6,
      },
    ],
  }), [categoryPerformance])

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        backgroundColor: "#1e293b",
        titleColor: "#f8fafc",
        bodyColor: "#cbd5e1",
        padding: 12,
        cornerRadius: 8,
      },
    },
    scales: {
      x: {
        ticks: { color: "#94a3b8", maxRotation: 45 },
        grid: { color: "rgba(148, 163, 184, 0.1)" },
      },
      y: {
        beginAtZero: true,
        max: 100,
        ticks: { color: "#94a3b8" },
        grid: { color: "rgba(148, 163, 184, 0.1)" },
      },
    },
  }

  const barOptions = {
    ...chartOptions,
    indexAxis: "y" as const,
    scales: {
      x: {
        beginAtZero: true,
        max: 100,
        ticks: { color: "#94a3b8" },
        grid: { color: "rgba(148, 163, 184, 0.1)" },
      },
      y: {
        ticks: { color: "#94a3b8" },
        grid: { display: false },
      },
    },
  }

  const dimAvg = (field: "correctnessScore" | "efficiencyScore" | "logicScore" | "clarityScore") =>
    avgDimension((s) => s[field])

  const dims = [
    { label: "Corrección", key: "correctnessScore" as const, color: "bg-emerald-500" },
    { label: "Eficiencia", key: "efficiencyScore" as const, color: "bg-blue-500" },
    { label: "Lógica", key: "logicScore" as const, color: "bg-violet-500" },
    { label: "Claridad", key: "clarityScore" as const, color: "bg-amber-500" },
  ]

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-32">
        <div className="text-center">
          <div className="mx-auto mb-3 h-8 w-8 animate-spin rounded-full border-2 border-emerald-500 border-t-transparent" />
          <p className="text-sm text-white/60">Cargando progreso...</p>
        </div>
      </div>
    )
  }

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
            <h1 className="font-['Work_Sans'] text-2xl font-bold text-white">Progreso</h1>
            <p className="mt-1 text-sm text-white/60">
              Tu evolución y estadísticas de entrenamiento.
            </p>
          </div>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-white/50">Sesiones</p>
              <p className="mt-1 text-3xl font-bold text-white">{(sessions ?? []).length}</p>
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
              <p className="text-sm text-white/50">Promedio</p>
              <p className="mt-1 text-3xl font-bold text-white">{averageScore.toFixed(1)}</p>
            </div>
            <BarChart3 className="h-8 w-8 text-violet-400" />
          </div>
        </div>
        <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-white/50">Tiempo total</p>
              <p className="mt-1 text-3xl font-bold text-white">{formatDuration(totalTime)}</p>
            </div>
            <Clock className="h-8 w-8 text-amber-400" />
          </div>
        </div>
      </div>

      <div className="grid gap-6 xl:grid-cols-[1fr_360px]">
        <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
          <div className="mb-4 flex items-center justify-between">
            <h2 className="font-['Work_Sans'] text-lg font-semibold text-white">Evolución de puntaje</h2>
            <div className="flex gap-1 rounded-md bg-white/10 p-0.5">
              <button
                onClick={() => setChartView("score")}
                className={`rounded-md px-3 py-1 text-xs font-medium transition-all ${
                  chartView === "score"
                    ? "bg-white text-[#16213e]"
                    : "text-white/60 hover:text-white"
                }`}
              >
                Puntaje
              </button>
              <button
                onClick={() => setChartView("dimensions")}
                className={`rounded-md px-3 py-1 text-xs font-medium transition-all ${
                  chartView === "dimensions"
                    ? "bg-white text-[#16213e]"
                    : "text-white/60 hover:text-white"
                }`}
              >
                Dimensiones
              </button>
            </div>
          </div>
          {completed.length === 0 ? (
            <div className="flex items-center justify-center py-16">
              <p className="text-sm text-white/40">Completa tu primer simulacro para ver tu evolución.</p>
            </div>
          ) : (
            <div className="h-64">
              {chartView === "score" ? (
                <Line data={scoreChartData} options={chartOptions} />
              ) : (
                <Bar data={categoryChartData} options={barOptions} />
              )}
            </div>
          )}
        </div>

        <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
          <h2 className="font-['Work_Sans'] mb-4 text-lg font-semibold text-white">Dimensiones</h2>
          {completed.length === 0 ? (
            <div className="flex items-center justify-center py-12">
              <p className="text-sm text-white/40">Sin datos aún.</p>
            </div>
          ) : (
            <div className="space-y-4">
              {dims.map((dim) => {
                const value = dimAvg(dim.key)
                return (
                  <div key={dim.key}>
                    <div className="mb-1 flex items-center justify-between text-sm">
                      <span className="text-white/70">{dim.label}</span>
                      <span className="font-mono text-white/90">{value.toFixed(1)}</span>
                    </div>
                    <div className="h-2 overflow-hidden rounded-full bg-white/10">
                      <div
                        className={`h-full rounded-full transition-all ${dim.color}`}
                        style={{ width: `${value}%` }}
                      />
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </div>
      </div>

      <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
        <h2 className="font-['Work_Sans'] mb-4 text-lg font-semibold text-white">Logros</h2>
        {!allAchievements || allAchievements.length === 0 ? (
          <div className="flex items-center justify-center py-12">
            <p className="text-sm text-white/40">No hay logros disponibles.</p>
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
            {allAchievements.map((achievement) => {
              const ua = userAchievements?.find((u) => u.achievementId === achievement.id)
              const unlocked = !!ua
              const formattedDate = ua?.unlockedAt
                ? new Intl.DateTimeFormat("es-CO", {
                    day: "2-digit",
                    month: "short",
                    year: "numeric",
                  }).format(new Date(ua.unlockedAt))
                : null
              return (
                <div
                  key={achievement.id}
                  className={`group relative rounded-lg border p-4 text-center transition-all ${
                    unlocked
                      ? "border-emerald-500/30 bg-emerald-500/10"
                      : "border-white/10 bg-white/5 opacity-50"
                  }`}
                >
                  <div
                    className={`mx-auto mb-2 flex h-12 w-12 items-center justify-center rounded-full text-xl ${
                      unlocked
                        ? "bg-emerald-500/20 text-emerald-400"
                        : "bg-white/10 text-white/30"
                    }`}
                  >
                    <Trophy className="h-6 w-6" />
                  </div>
                  <p
                    className={`text-sm font-medium ${
                      unlocked ? "text-white" : "text-white/50"
                    }`}
                  >
                    {achievement.name}
                  </p>
                  <p className="mt-0.5 text-xs text-white/40">
                    {achievement.description ?? ""}
                  </p>
                  {unlocked ? (
                    <div className="mt-2 space-y-1">
                      <span className="inline-block rounded-full bg-emerald-500/20 px-2 py-0.5 text-xs font-medium text-emerald-400">
                        Desbloqueado
                      </span>
                      {formattedDate && (
                        <p className="text-[10px] text-emerald-400/60">
                          {formattedDate}
                        </p>
                      )}
                      {achievement.pointsReward != null && achievement.pointsReward > 0 && (
                        <p className="text-[10px] font-medium text-amber-400/70">
                          +{achievement.pointsReward} pts
                        </p>
                      )}
                    </div>
                  ) : (
                    <p className="mt-2 text-[10px] text-white/30">
                      {achievement.unlockCriteria ?? ""}
                    </p>
                  )}
                </div>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}
