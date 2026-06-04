import { Users, HelpCircle, PlayCircle, Trophy, TrendingUp, UserCheck, UserPlus, Activity } from "lucide-react"
import { useAdminDashboard } from "@/api/admin.queries"

function StatCard({ icon: Icon, label, value, color }: { icon: typeof Users; label: string; value: number | string; color: string }) {
  return (
    <div className="bg-white rounded-xl p-6 border border-gray-100 shadow-sm hover:shadow-md transition-all">
      <div className="flex items-center justify-between mb-4">
        <div className={`inline-flex h-12 w-12 items-center justify-center rounded-lg ${color}`}>
          <Icon className="h-6 w-6" />
        </div>
      </div>
      <div className="font-['Work_Sans'] font-bold text-3xl text-gray-900 mb-1">{value}</div>
      <div className="text-sm text-gray-500">{label}</div>
    </div>
  )
}

export default function AdminDashboard() {
  const { data, isLoading, isError } = useAdminDashboard()

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-center">
          <div className="h-8 w-8 border-2 border-emerald-500 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
          <p className="text-white/60">Cargando dashboard...</p>
        </div>
      </div>
    )
  }

  if (isError || !data) {
    return (
      <div className="flex items-center justify-center h-full">
        <p className="text-red-400">Error al cargar el dashboard</p>
      </div>
    )
  }

  return (
    <div>
      <h1 className="font-['Work_Sans'] font-bold text-2xl text-white mb-6">Dashboard Admin</h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard icon={Users} label="Usuarios totales" value={data.totalUsers} color="text-blue-600 bg-blue-50" />
        <StatCard icon={UserPlus} label="Estudiantes" value={data.studentsCount} color="text-emerald-600 bg-emerald-50" />
        <StatCard icon={UserCheck} label="Profesionales" value={data.professionalsCount} color="text-violet-600 bg-violet-50" />
        <StatCard icon={Activity} label="Admins" value={data.adminsCount} color="text-rose-600 bg-rose-50" />
        <StatCard icon={PlayCircle} label="Sesiones totales" value={data.totalSessions} color="text-cyan-600 bg-cyan-50" />
        <StatCard icon={TrendingUp} label="Sesiones hoy" value={data.sessionsToday} color="text-amber-600 bg-amber-50" />
        <StatCard icon={HelpCircle} label="Preguntas activas" value={data.activeQuestions} color="text-indigo-600 bg-indigo-50" />
        <StatCard icon={Trophy} label="Logros" value={data.totalAchievements} color="text-orange-600 bg-orange-50" />
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl p-6 border border-gray-100 shadow-sm">
          <h2 className="font-['Work_Sans'] font-semibold text-lg text-gray-900 mb-4">Resumen rápido</h2>
          <div className="space-y-4">
            {[
              { label: "Categorías activas", value: data.activeCategories, total: data.totalCategories },
              { label: "Sesiones completadas", value: data.completedSessions, total: data.totalSessions },
              { label: "Sesiones en progreso", value: data.inProgressSessions, total: data.totalSessions },
              { label: "Usuarios activos", value: data.activeUsers, total: data.totalUsers },
            ].map((item) => (
              <div key={item.label}>
                <div className="flex justify-between text-sm mb-1">
                  <span className="text-gray-600">{item.label}</span>
                  <span className="font-medium text-gray-900">{item.value}/{item.total}</span>
                </div>
                <div className="h-2 bg-gray-100 rounded-full overflow-hidden">
                  <div
                    className="h-full bg-emerald-500 rounded-full transition-all"
                    style={{ width: `${item.total > 0 ? (item.value / item.total) * 100 : 0}%` }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-xl p-6 border border-gray-100 shadow-sm">
          <h2 className="font-['Work_Sans'] font-semibold text-lg text-gray-900 mb-4">Últimos usuarios registrados</h2>
          <div className="space-y-3">
            {data.recentUsers.length === 0 && (
              <p className="text-sm text-gray-400">No hay usuarios registrados</p>
            )}
            {data.recentUsers.map((user) => (
              <div key={user.id} className="flex items-center justify-between py-2 border-b border-gray-50 last:border-0">
                <div className="flex items-center gap-3">
                  <div className="h-8 w-8 rounded-full bg-gradient-to-br from-emerald-400 to-blue-500 flex items-center justify-center text-white text-xs font-bold">
                    {user.fullName.charAt(0).toUpperCase()}
                  </div>
                  <div>
                    <div className="text-sm font-medium text-gray-900">{user.fullName}</div>
                    <div className="text-xs text-gray-400">{user.email}</div>
                  </div>
                </div>
                <div className="text-right">
                  <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                    user.role === "ADMIN" ? "bg-rose-50 text-rose-700" :
                    user.role === "PROFESSIONAL" ? "bg-violet-50 text-violet-700" :
                    "bg-emerald-50 text-emerald-700"
                  }`}>
                    {user.role === "STUDENT" ? "Estudiante" : user.role === "PROFESSIONAL" ? "Profesional" : "Admin"}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
