import { useAuth } from "@/context/AuthContext"
import { useNavigate } from "react-router-dom"

type Props = {
  current: string;
  onChange: (page: string) => void;
};

const items = [
  { key: "default", label: "Inicio" },
  { key: "questions", label: "Preguntas" },
  { key: "sessions", label: "Sesiones" },
  { key: "ranking", label: "Ranking" },
] as const;

export default function SidebarMenu({ current, onChange }: Props) {
  const { logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate("/login")
  }

  return (
    <div className="flex flex-col h-full">
      <h2 className="text-xl font-bold mb-6">DevMock</h2>

      <nav className="flex flex-col gap-1 flex-1">
        {items.map(({ key, label }) => (
          <button
            key={key}
            className={`text-left rounded-md px-3 py-2 text-sm font-medium transition-colors ${
              current === key
                ? "bg-neutral-900 text-white"
                : "text-neutral-700 hover:bg-neutral-100"
            }`}
            onClick={() => onChange(key)}
          >
            {label}
          </button>
        ))}
      </nav>

      <button
        onClick={handleLogout}
        className="text-left rounded-md px-3 py-2 text-sm font-medium text-red-600 hover:bg-red-50 transition-colors"
      >
        Cerrar sesión
      </button>
    </div>
  )
}
