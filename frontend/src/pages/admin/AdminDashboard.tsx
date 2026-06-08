import { Users, HelpCircle, PlayCircle, Trophy, TrendingUp, UserCheck, UserPlus, Activity, ArrowUpRight } from "lucide-react"
import { useAdminDashboard } from "@/api/admin.queries"

const accentColors = [
  { bar: "from-blue-400 to-blue-600", text: "text-blue-400", glow: "shadow-blue-500/20" },
  { bar: "from-emerald-400 to-emerald-600", text: "text-emerald-400", glow: "shadow-emerald-500/20" },
  { bar: "from-violet-400 to-violet-600", text: "text-violet-400", glow: "shadow-violet-500/20" },
  { bar: "from-rose-400 to-rose-600", text: "text-rose-400", glow: "shadow-rose-500/20" },
  { bar: "from-cyan-400 to-cyan-600", text: "text-cyan-400", glow: "shadow-cyan-500/20" },
  { bar: "from-amber-400 to-amber-600", text: "text-amber-400", glow: "shadow-amber-500/20" },
  { bar: "from-indigo-400 to-indigo-600", text: "text-indigo-400", glow: "shadow-indigo-500/20" },
  { bar: "from-orange-400 to-orange-600", text: "text-orange-400", glow: "shadow-orange-500/20" },
]

function StatCard({ icon: Icon, label, value, accent }: { icon: typeof Users; label: string; value: number | string; accent: typeof accentColors[0] }) {
  return (
    <div className={`relative bg-white/[0.04] backdrop-blur-sm rounded-xl border border-white/[0.06] p-5 hover:bg-white/[0.07] transition-all group shadow-lg ${accent.glow}`}>
      <div className={`absolute top-0 left-4 right-4 h-[2px] rounded-full bg-gradient-to-r ${accent.bar} opacity-60`} />
      <div className="flex items-center justify-between mb-3">
        <div className={`h-10 w-10 rounded-lg bg-white/[0.06] flex items-center justify-center ${accent.text} group-hover:bg-white/[0.1] transition-colors`}>
          <Icon className="h-5 w-5" />
        </div>
        <ArrowUpRight className={`h-4 w-4 ${accent.text} opacity-0 group-hover:opacity-100 transition-opacity`} />
      </div>
      <div className="font-['Work_Sans'] font-bold text-2xl sm:text-3xl text-white mb-0.5 tabular-nums">{value}</div>
      <div className="text-sm text-white/50">{label}</div>
    </div>
  )
}

function ProgressBar({ label, value, total, color }: { label: string; value: number; total: number; color: string }) {
  const pct = total > 0 ? (value / total) * 100 : 0
  return (
    <div>
      <div className="flex justify-between text-sm mb-1.5">
        <span className="text-white/60">{label}</span>
        <span className="font-medium text-white/80 tabular-nums">{value}/{total}</span>
      </div>
      <div className="h-2 bg-white/[0.06] rounded-full overflow-hidden ring-1 ring-inset ring-white/[0.04]">
        <div
          className={`h-full rounded-full bg-gradient-to-r ${color} transition-all duration-500`}
          style={{ width: `${pct}%` }}
        />
      </div>
    </div>
  )
}

export default function AdminDashboard() {
  const { data, isLoading, isError } = useAdminDashboard()

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="text-center">
          <div className="h-10 w-10 border-2 border-emerald-400 border-t-transparent rounded-full animate-spin mx-auto mb-4" />
          <p className="text-white/50 text-sm">Cargando dashboard...</p>
        </div>
      </div>
    )
  }

  if (isError || !data) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <div className="text-center">
          <div className="h-12 w-12 rounded-full bg-red-500/10 flex items-center justify-center mx-auto mb-4">
            <HelpCircle className="h-6 w-6 text-red-400" />
          </div>
          <p className="text-red-400 text-sm font-medium">Error al cargar el dashboard</p>
          <p className="text-white/40 text-xs mt-1">Intenta recargar la página</p>
        </div>
      </div>
    )
  }

  const statCards = [
    { icon: Users, label: "Usuarios totales", value: data.totalUsers },
    { icon: UserPlus, label: "Estudiantes", value: data.studentsCount },
    { icon: UserCheck, label: "Profesionales", value: data.professionalsCount },
    { icon: Activity, label: "Admins", value: data.adminsCount },
    { icon: PlayCircle, label: "Sesiones totales", value: data.totalSessions },
    { icon: TrendingUp, label: "Sesiones hoy", value: data.sessionsToday },
    { icon: HelpCircle, label: "Preguntas activas", value: data.activeQuestions },
    { icon: Trophy, label: "Logros", value: data.totalAchievements },
  ]

  return (
    <div>
      <div className="flex items-center gap-3 mb-8">
        <div className="h-8 w-1 rounded-full bg-gradient-to-b from-emerald-400 to-emerald-600" />
        <h1 className="font-['Work_Sans'] font-bold text-xl sm:text-2xl text-white">Dashboard Admin</h1>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {statCards.map((card, idx) => (
          <StatCard key={card.label} {...card} accent={accentColors[idx]} />
        ))}
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        <div className="bg-white/[0.04] backdrop-blur-sm rounded-xl border border-white/[0.06] p-6">
          <div className="flex items-center gap-2 mb-6">
            <div className="h-6 w-6 rounded-md bg-emerald-500/10 flex items-center justify-center">
              <Activity className="h-3.5 w-3.5 text-emerald-400" />
            </div>
            <h2 className="font-['Work_Sans'] font-semibold text-base text-white">Resumen rápido</h2>
          </div>
          <div className="space-y-5">
            <ProgressBar label="Categorías activas" value={data.activeCategories} total={data.totalCategories} color="from-emerald-400 to-emerald-500" />
            <ProgressBar label="Sesiones completadas" value={data.completedSessions} total={data.totalSessions} color="from-blue-400 to-blue-500" />
            <ProgressBar label="Sesiones en progreso" value={data.inProgressSessions} total={data.totalSessions} color="from-amber-400 to-amber-500" />
            <ProgressBar label="Usuarios activos" value={data.activeUsers} total={data.totalUsers} color="from-violet-400 to-violet-500" />
          </div>
        </div>

        <div className="bg-white/[0.04] backdrop-blur-sm rounded-xl border border-white/[0.06] p-6">
          <div className="flex items-center gap-2 mb-6">
            <div className="h-6 w-6 rounded-md bg-blue-500/10 flex items-center justify-center">
              <Users className="h-3.5 w-3.5 text-blue-400" />
            </div>
            <h2 className="font-['Work_Sans'] font-semibold text-base text-white">Últimos usuarios registrados</h2>
          </div>
          <div className="space-y-1">
            {data.recentUsers.length === 0 && (
              <p className="text-sm text-white/40 text-center py-8">No hay usuarios registrados</p>
            )}
            {data.recentUsers.map((user) => (
              <div key={user.id} className="flex items-center justify-between py-2.5 px-2 rounded-lg hover:bg-white/[0.03] transition-colors">
                <div className="flex items-center gap-3 min-w-0">
                  <div className="h-8 w-8 rounded-full bg-gradient-to-br from-emerald-400 to-blue-500 flex items-center justify-center text-white text-xs font-bold flex-shrink-0 shadow-lg shadow-emerald-500/20">
                    {user.fullName.charAt(0).toUpperCase()}
                  </div>
                  <div className="min-w-0">
                    <div className="text-sm font-medium text-white/90 truncate">{user.fullName}</div>
                    <div className="text-xs text-white/40 truncate">{user.email}</div>
                  </div>
                </div>
                <span className={`text-[11px] px-2.5 py-1 rounded-full font-medium flex-shrink-0 ${
                  user.role === "ADMIN" ? "bg-rose-500/10 text-rose-300 ring-1 ring-inset ring-rose-500/20" :
                  user.role === "PROFESSIONAL" ? "bg-violet-500/10 text-violet-300 ring-1 ring-inset ring-violet-500/20" :
                  "bg-emerald-500/10 text-emerald-300 ring-1 ring-inset ring-emerald-500/20"
                }`}>
                  {user.role === "STUDENT" ? "Estudiante" : user.role === "PROFESSIONAL" ? "Profesional" : "Admin"}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
