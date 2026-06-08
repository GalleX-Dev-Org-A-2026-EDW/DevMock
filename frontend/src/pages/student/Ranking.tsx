import { useMemo, useState } from "react"
import { useRankings } from "@/api/rankings.queries"
import { useMe } from "@/api/users.queries"
import { ArrowLeft, Medal, Trophy, TrendingUp, Calendar } from "lucide-react"
import type { RankingPeriod } from "@/api/enums"

type Props = {
  onBack: () => void
}

type PeriodTab = RankingPeriod

const TABS: Array<{ key: PeriodTab; label: string }> = [
  { key: "ALL_TIME", label: "General" },
  { key: "MONTHLY", label: "Mensual" },
  { key: "WEEKLY", label: "Semanal" },
]

function PositionBadge({ rank }: { rank: number }) {
  if (rank === 1) return <span className="text-lg">🥇</span>
  if (rank === 2) return <span className="text-lg">🥈</span>
  if (rank === 3) return <span className="text-lg">🥉</span>
  return (
    <span className="font-mono text-sm font-bold text-white/50">
      #{rank}
    </span>
  )
}

function ScoreBar({ score }: { score: number }) {
  const pct = Math.min(100, Math.max(0, score))
  return (
    <div className="h-1.5 w-20 overflow-hidden rounded-full bg-white/10">
      <div
        className="h-full rounded-full bg-gradient-to-r from-emerald-500 to-emerald-400"
        style={{ width: `${pct}%` }}
      />
    </div>
  )
}

export default function Ranking({ onBack }: Props) {
  const { data: rankings, isLoading: rLoading } = useRankings()
  const { data: currentUserData } = useMe()
  const [period, setPeriod] = useState<PeriodTab>("ALL_TIME")

  const currentUserId = currentUserData?.id

  const globalRankings = useMemo(() => {
    if (!rankings) return []
    return rankings
      .filter((r) => r.period === period && r.categoryId == null && r.difficultyId == null)
      .sort((a, b) => (a.rankPosition ?? 999) - (b.rankPosition ?? 999))
  }, [rankings, period])

  const currentUserEntry = useMemo(
    () => globalRankings.find((r) => r.userId === currentUserId),
    [globalRankings, currentUserId],
  )

  const topEntries = useMemo(
    () => globalRankings.slice(0, 10),
    [globalRankings],
  )

  if (rLoading) {
    return (
      <div className="flex items-center justify-center py-32">
        <div className="text-center">
          <div className="mx-auto mb-3 h-8 w-8 animate-spin rounded-full border-2 border-emerald-500 border-t-transparent" />
          <p className="text-sm text-white/60">Cargando ranking...</p>
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
            <h1 className="font-['Work_Sans'] text-2xl font-bold text-white">Ranking</h1>
            <p className="mt-1 text-sm text-white/60">
              Compara tu desempeño con otros estudiantes.
            </p>
          </div>
        </div>
      </div>

      <div className="flex gap-1 rounded-lg bg-white/10 p-1">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setPeriod(tab.key)}
            className={`flex-1 rounded-md px-4 py-2 text-sm font-medium transition-all ${
              period === tab.key
                ? "bg-white text-[#16213e] shadow-sm"
                : "text-white/60 hover:text-white"
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {currentUserEntry && (
        <div className="rounded-lg border border-emerald-500/30 bg-emerald-500/10 p-4 backdrop-blur-sm">
          <div className="flex items-center gap-3">
            <Trophy className="h-5 w-5 text-emerald-400" />
            <div>
              <p className="text-sm font-medium text-white">
                Tu posición: <span className="font-bold">#{currentUserEntry.rankPosition}</span> de {globalRankings.length}
              </p>
              <p className="mt-0.5 text-xs text-emerald-400/70">
                Puntaje total: {currentUserEntry.totalScore?.toFixed(1) ?? "—"} · {currentUserEntry.totalSessions ?? 0} sesiones
              </p>
            </div>
          </div>
        </div>
      )}

      {globalRankings.length === 0 ? (
        <div className="rounded-lg border border-dashed border-white/20 p-16 text-center">
          <Medal className="mx-auto mb-4 h-12 w-12 text-white/30" />
          <p className="text-lg font-medium text-white/70">
            No hay datos de ranking
          </p>
          <p className="mt-2 text-sm text-white/40">
            {period === "ALL_TIME"
              ? "Completa sesiones para aparecer en la clasificación general."
              : "No hay datos para este período. Vuelve más tarde o cambia de filtro."}
          </p>
        </div>
      ) : (
        <div className="overflow-hidden rounded-lg border border-white/10 bg-white/10 backdrop-blur-sm">
          <table className="w-full">
            <thead>
              <tr className="border-b border-white/10 text-left text-xs font-medium uppercase tracking-wider text-white/40">
                <th className="px-4 py-3" style={{ width: 60 }}>#</th>
                <th className="px-4 py-3">Estudiante</th>
                <th className="px-4 py-3 text-right">Puntaje</th>
                <th className="px-4 py-3 text-right">Sesiones</th>
                <th className="hidden px-4 py-3 text-right sm:table-cell">Progreso</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5">
              {topEntries.map((entry) => {
                const isMe = entry.userId === currentUserId
                return (
                  <tr
                    key={entry.id}
                    className={`transition-colors ${
                      isMe
                        ? "bg-emerald-500/10"
                        : "hover:bg-white/5"
                    }`}
                  >
                    <td className="px-4 py-3">
                      <PositionBadge rank={entry.rankPosition ?? 0} />
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2">
                        <div className="flex h-7 w-7 items-center justify-center rounded-full bg-gradient-to-br from-emerald-400 to-blue-500 text-xs font-bold text-white">
                          {(entry.userName ?? "?").charAt(0).toUpperCase()}
                        </div>
                        <div>
                          <p className={`text-sm font-medium ${isMe ? "text-emerald-300" : "text-white"}`}>
                            {entry.userName ?? "Usuario"}
                            {entry.userRole === "PROFESSIONAL" && (
                              <span className="ml-1.5 rounded bg-blue-500/20 px-1.5 py-0.5 text-[10px] font-medium text-blue-400">
                                PRO
                              </span>
                            )}
                            {entry.userRole === "STUDENT" && (
                              <span className="ml-1.5 rounded bg-emerald-500/20 px-1.5 py-0.5 text-[10px] font-medium text-emerald-400">
                                STUDENT
                              </span>
                            )}
                            {isMe && <span className="ml-1.5 text-xs text-emerald-400/60">(tú)</span>}
                          </p>
                        </div>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-right font-mono text-sm font-semibold text-white">
                      {entry.totalScore?.toFixed(1) ?? "—"}
                    </td>
                    <td className="px-4 py-3 text-right font-mono text-sm text-white/60">
                      {entry.totalSessions ?? 0}
                    </td>
                    <td className="hidden px-4 py-3 text-right sm:table-cell">
                      <div className="flex items-center justify-end">
                        <ScoreBar score={entry.totalScore ?? 0} />
                      </div>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>

          {globalRankings.length > 10 && (
            <div className="border-t border-white/10 px-4 py-3 text-center text-xs text-white/40">
              Mostrando los 10 mejores de {globalRankings.length} estudiantes
            </div>
          )}
        </div>
      )}

      {globalRankings.length > 0 && (
        <div className="rounded-lg border border-white/10 bg-white/10 p-4 backdrop-blur-sm">
          <h3 className="mb-3 flex items-center gap-2 text-sm font-semibold text-white">
            <TrendingUp className="h-4 w-4 text-emerald-400" />
            Leyenda
          </h3>
          <div className="grid gap-2 text-xs text-white/50 sm:grid-cols-3">
            <div className="flex items-center gap-2">
              <Calendar className="h-3.5 w-3.5" />
              <span>
                {period === "ALL_TIME" ? "Clasificación histórica acumulada" :
                 period === "MONTHLY" ? "Clasificación del mes actual" :
                 "Clasificación de la semana actual"}
              </span>
            </div>
            <div className="flex items-center gap-2">
              <Medal className="h-3.5 w-3.5" />
              <span>Las medallas indican los 3 primeros puestos</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="h-3 w-3 rounded bg-emerald-500/30" />
              <span>Tu fila está resaltada en verde</span>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
