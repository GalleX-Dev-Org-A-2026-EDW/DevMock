import { useAuth } from "@/context/AuthContext"
import { useNavigate } from "react-router-dom"
import { LogOut } from "lucide-react"
import devMockIcon from "@/assets/DevMockIcono.png"

export default function SidebarMenu() {
  const { logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate("/login")
  }

  return (
    <div className="flex flex-col h-full">
      <div className="flex items-center gap-3 mb-10">
        <img src={devMockIcon} alt="DevMock" className="h-9 w-9 brightness-0 invert" />
        <span className="font-['Work_Sans'] font-bold text-lg text-white">DevMock</span>
      </div>

      <div className="flex-1" />

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
