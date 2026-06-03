import MainLayout from "@/layouts/MainLayout"
import PrivateRoute from "@/components/PrivateRoute"
import LoginPage from "@/pages/LoginPage"
import { useAuth } from "@/context/AuthContext"

export default function App() {
  const { user, logout } = useAuth()

  return (
    <PrivateRoute fallback={<LoginPage />}>
      <MainLayout
        sidebar={
          <div className="space-y-4">
            <h2 className="text-xl font-bold">DevMock</h2>
            <p className="text-sm text-neutral-500">Menú próximo a definir</p>
          </div>
        }
        content={
          <div className="space-y-4">
            <header className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-neutral-600">
                Bienvenido, {user?.username ?? "usuario"}
              </h2>
              <button
                onClick={logout}
                className="rounded-md px-3 py-1.5 text-sm text-red-600 hover:bg-red-50 transition-colors"
              >
                Cerrar sesión
              </button>
            </header>
            <p className="text-muted-foreground">Contenido próximo a definir.</p>
          </div>
        }
      />
    </PrivateRoute>
  )
}
