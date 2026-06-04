import { useAuth } from "@/context/AuthContext"
import { useNavigate, useLocation } from "react-router-dom"
import {
  LogOut,
  LayoutDashboard,
  Users,
  HelpCircle,
  FolderTree,
  Award,
  BarChart3,
  BrainCircuit,
  ListChecks,
  ClipboardList,
  type LucideIcon,
} from "lucide-react"
import devMockIcon from "@/assets/DevMockIcono.png"

interface NavItem {
  label: string
  icon: LucideIcon
  path: string
}

const navItems: NavItem[] = [
  { label: "Dashboard", icon: LayoutDashboard, path: "/admin" },
  { label: "Usuarios", icon: Users, path: "/admin/users" },
  { label: "Preguntas", icon: HelpCircle, path: "/admin/questions" },
  { label: "Categorías", icon: FolderTree, path: "/admin/categories" },
  { label: "Logros", icon: Award, path: "/admin/achievements" },
  { label: "Dificultades", icon: BarChart3, path: "/admin/difficulty-levels" },
  { label: "Tipos entrevista", icon: BrainCircuit, path: "/admin/interview-types" },
  { label: "Criterios eval.", icon: ListChecks, path: "/admin/evaluation-criteria" },
  { label: "Auditoría", icon: ClipboardList, path: "/admin/audit-logs" },
]

export default function AdminSidebar() {
  const { logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const handleLogout = () => {
    logout()
    navigate("/login")
  }

  return (
    <div className="flex flex-col h-full">
      <div className="flex items-center gap-3 mb-8">
        <img src={devMockIcon} alt="DevMock" className="h-9 w-9 brightness-0 invert" />
        <span className="font-['Work_Sans'] font-bold text-lg text-white">DevMock</span>
      </div>

      <nav className="flex-1 space-y-1">
        {navItems.map((item) => {
          const isActive = location.pathname === item.path
          return (
            <button
              key={item.path}
              onClick={() => navigate(item.path)}
              className={`flex items-center gap-3 w-full rounded-lg px-3 py-2.5 text-sm font-medium transition-all ${
                isActive
                  ? "bg-white/10 text-white"
                  : "text-white/70 hover:text-white hover:bg-white/5"
              }`}
            >
              <item.icon className="h-4 w-4" />
              {item.label}
            </button>
          )
        })}
      </nav>

      <button
        onClick={handleLogout}
        className="flex items-center gap-2 rounded-lg px-3 py-2.5 text-sm font-medium text-white/70 hover:text-white hover:bg-white/10 transition-all"
      >
        <LogOut className="h-4 w-4" />
        Cerrar sesión
      </button>
    </div>
  )
}
