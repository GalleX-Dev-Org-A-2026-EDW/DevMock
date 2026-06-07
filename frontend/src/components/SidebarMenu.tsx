import { useAuth } from "@/context/AuthContext"
import { useNavigate } from "react-router-dom"
import { BarChart3, ClipboardList, Home, LogOut, PlayCircle, Plus, Trophy, Medal } from "lucide-react"
import devMockIcon from "@/assets/DevMockIcono.png"

export type StudentMenuItem = "home" | "create" | "sessions" | "progress" | "ranking"

type Props = {
  activeItem: StudentMenuItem
  totalSessions: number
  completedSessions: number
  averageScore: number
  onSelect: (item: StudentMenuItem) => void
}

const items: Array<{
  id: StudentMenuItem
  label: string
  icon: typeof Home
}> = [
  { id: "home", label: "Inicio", icon: Home },
  { id: "create", label: "Nueva entrevista", icon: Plus },
  { id: "sessions", label: "Mis sesiones", icon: ClipboardList },
  { id: "progress", label: "Progreso", icon: BarChart3 },
  { id: "ranking", label: "Ranking", icon: Medal },
]

export default function SidebarMenu({
  activeItem,
  totalSessions,
  completedSessions,
  averageScore,
  onSelect,
}: Props) {
  const { logout, user } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate("/login")
  }

  return (
    <div className="flex h-full flex-col">
      <div className="mb-8 flex items-center gap-3">
        <img src={devMockIcon} alt="DevMock" className="h-9 w-9 brightness-0 invert" />
        <span className="font-['Work_Sans'] text-lg font-bold text-white">DevMock</span>
      </div>

      <div className="mb-6 rounded-lg border border-white/10 bg-white/10 p-3">
        <p className="truncate text-sm font-semibold text-white">{user?.username ?? "Estudiante"}</p>
        <p className="mt-0.5 text-xs text-white/50">Plan de entrenamiento</p>
      </div>

      <nav className="space-y-1">
        {items.map((item) => {
          const Icon = item.icon
          const active = activeItem === item.id

          return (
            <button
              key={item.id}
              onClick={() => onSelect(item.id)}
              className={`flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-left text-sm font-medium transition-all ${
                active
                  ? "bg-white text-[#16213e] shadow-sm"
                  : "text-white/70 hover:bg-white/10 hover:text-white"
              }`}
            >
              <Icon className="h-4 w-4" />
              {item.label}
            </button>
          )
        })}
      </nav>

      <div className="mt-6 space-y-3 rounded-lg border border-white/10 bg-white/10 p-3">
        <div className="flex items-center gap-2 text-white">
          <Trophy className="h-4 w-4 text-emerald-300" />
          <span className="text-sm font-semibold">Resumen</span>
        </div>
        <div className="grid grid-cols-2 gap-2">
          <div className="rounded-md bg-white/10 p-2">
            <p className="text-lg font-bold text-white">{totalSessions}</p>
            <p className="text-[11px] text-white/50">Sesiones</p>
          </div>
          <div className="rounded-md bg-white/10 p-2">
            <p className="text-lg font-bold text-white">{completedSessions}</p>
            <p className="text-[11px] text-white/50">Hechas</p>
          </div>
        </div>
        <div className="rounded-md bg-white/10 p-2">
          <div className="flex items-center justify-between">
            <span className="text-xs text-white/60">Promedio</span>
            <span className="font-mono text-sm font-bold text-white">{averageScore.toFixed(1)}</span>
          </div>
          <div className="mt-2 h-1.5 overflow-hidden rounded-full bg-white/15">
            <div
              className="h-full rounded-full bg-emerald-400"
              style={{ width: `${Math.min(100, Math.max(0, averageScore))}%` }}
            />
          </div>
        </div>
      </div>

      <div className="mt-auto pt-6">
        <button
          onClick={() => onSelect("create")}
          className="mb-3 flex w-full items-center justify-center gap-2 rounded-lg bg-emerald-500 px-3 py-2.5 text-sm font-semibold text-white shadow-sm transition-all hover:bg-emerald-600"
        >
          <PlayCircle className="h-4 w-4" />
          Iniciar práctica
        </button>
        <button
          onClick={handleLogout}
          className="flex w-full items-center gap-2 rounded-lg px-3 py-2.5 text-sm font-medium text-white/70 transition-all hover:bg-white/10 hover:text-white"
        >
          <LogOut className="h-4 w-4" />
          Cerrar sesión
        </button>
      </div>
    </div>
  )
}
