import { useState, type FormEvent } from "react"
import { Link, useNavigate } from "react-router-dom"
import { ChevronLeft, Mail, Lock, AlertCircle, Eye, EyeOff } from "lucide-react"
import { authApi } from "@/api/auth"
import { useAuth } from "@/context/AuthContext"
import { ApiError } from "@/api/http"
import type { UserRole } from "@/api/auth"
import devMockIcon from "@/assets/DevMockIcono.png"

interface FieldErrors {
  email?: string
  password?: string
}

function extractFieldErrors(err: unknown): FieldErrors {
  if (err instanceof ApiError && err.fields) {
    const map: Record<string, string> = {
      email: err.fields.email,
      password: err.fields.password,
    }
    return Object.fromEntries(Object.entries(map).filter(([, v]) => v))
  }
  return {}
}

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) {
    if (err.status === 401) return err.message
    if (err.status === 404) return err.message
    if (err.status === 403) return "Acceso denegado. Intenta de nuevo."
    if (err.code === "VALIDATION_ERROR") return "Corrige los errores en el formulario."
    return err.message
  }
  if (err instanceof TypeError && err.message === "Failed to fetch") {
    return "No se pudo conectar con el servidor. Verifica tu conexión."
  }
  return err instanceof Error ? err.message : "Error inesperado. Intenta de nuevo."
}

export default function LoginPage() {
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState("")
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
  const [loading, setLoading] = useState(false)

  const { login } = useAuth()
  const navigate = useNavigate()

  const validate = (): boolean => {
    const fields: FieldErrors = {}
    if (!email.trim()) fields.email = "El email es requerido."
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) fields.email = "Email inválido"
    if (!password) fields.password = "La contraseña es requerida."
    setFieldErrors(fields)
    return Object.keys(fields).length === 0
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError("")
    if (!validate()) return
    setLoading(true)
    try {
      const res = await authApi.login({ email, password })
      login(res.token, res.fullName, res.role as UserRole)
      navigate(res.role === "ADMIN" ? "/admin" : "/dashboard")
    } catch (err) {
      setError(errorMessage(err))
      setFieldErrors(extractFieldErrors(err))
    } finally {
      setLoading(false)
    }
  }

  const inputClass = (hasError: boolean) =>
    `w-full pl-10 pr-10 py-2.5 rounded-lg border bg-white text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:border-transparent transition-all ${
      hasError
        ? "border-red-400 focus:ring-red-400"
        : "border-border focus:ring-primary"
    }`

  return (
    <div className="min-h-screen bg-slate-100 font-['Manrope'] flex items-center justify-center p-6">
      <Link
        to="/"
        className="absolute top-6 left-6 flex items-center gap-2 text-sm font-medium text-muted-foreground hover:text-foreground transition-colors"
      >
        <ChevronLeft className="h-4 w-4" />
        Volver al inicio
      </Link>

      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="flex items-center justify-center gap-3 mb-4">
            <img src={devMockIcon} alt="DevMock" className="h-12 w-12" />
            <span className="font-['Work_Sans'] font-bold text-2xl text-foreground">DevMock</span>
          </div>
          <h1 className="font-['Work_Sans'] font-bold text-3xl text-foreground mb-2">
            Iniciar sesión
          </h1>
          <p className="text-sm text-muted-foreground">
            Continúa tu preparación para entrevistas técnicas
          </p>
        </div>

        <div className="bg-white rounded-2xl shadow-lg p-8 border border-border">
          <form onSubmit={handleSubmit} className="space-y-5" noValidate>
            {error && !Object.keys(fieldErrors).length && (
              <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 flex items-start gap-3">
                <AlertCircle className="h-5 w-5 flex-shrink-0 mt-0.5" />
                <p className="text-sm">{error}</p>
              </div>
            )}

            <div>
              <label htmlFor="email" className="block text-sm font-medium text-foreground mb-2">
                Email
              </label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(e) => { setEmail(e.target.value); setFieldErrors((p) => ({ ...p, email: undefined })) }}
                  placeholder="tu@email.com"
                  required
                  className={inputClass(!!fieldErrors.email)}
                  autoComplete="email"
                />
              </div>
              {fieldErrors.email && (
                <p className="text-xs text-red-500 mt-1">{fieldErrors.email}</p>
              )}
            </div>

            <div>
              <div className="flex items-center justify-between mb-2">
                <label htmlFor="password" className="block text-sm font-medium text-foreground">
                  Contraseña
                </label>
                <a href="#" className="text-xs font-medium text-primary hover:text-primary/80 transition-colors">
                  ¿Olvidaste tu contraseña?
                </a>
              </div>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => { setPassword(e.target.value); setFieldErrors((p) => ({ ...p, password: undefined })) }}
                  placeholder="••••••••"
                  required
                  className={inputClass(!!fieldErrors.password)}
                  autoComplete="current-password"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((p) => !p)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                  tabIndex={-1}
                >
                  {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
              {fieldErrors.password && (
                <p className="text-xs text-red-500 mt-1">{fieldErrors.password}</p>
              )}
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full px-4 py-3 bg-primary hover:bg-primary/90 text-primary-foreground rounded-lg font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-sm hover:shadow-md"
            >
              {loading ? "Iniciando sesión..." : "Iniciar sesión"}
            </button>

            {error && !!Object.keys(fieldErrors).length && (
              <p className="text-xs text-red-500 text-center">{error}</p>
            )}
          </form>

          <div className="relative my-6">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-border"></div>
            </div>
            <div className="relative flex justify-center text-xs">
              <span className="px-2 bg-white text-muted-foreground">O continúa con</span>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <button
              type="button"
              disabled
              className="flex items-center justify-center gap-2 px-4 py-2.5 bg-white border border-border rounded-lg text-sm font-medium text-muted-foreground cursor-not-allowed opacity-60"
            >
              <svg className="h-5 w-5" viewBox="0 0 24 24">
                <path fill="currentColor" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
                <path fill="currentColor" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
                <path fill="currentColor" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" />
                <path fill="currentColor" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" />
              </svg>
              Google
            </button>
            <button
              type="button"
              disabled
              className="flex items-center justify-center gap-2 px-4 py-2.5 bg-white border border-border rounded-lg text-sm font-medium text-muted-foreground cursor-not-allowed opacity-60"
            >
              <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z" />
              </svg>
              GitHub
            </button>
          </div>
        </div>

        <p className="text-center text-sm text-muted-foreground mt-6">
          ¿No tienes una cuenta?{" "}
          <Link to="/register" className="font-medium text-primary hover:text-primary/80 transition-colors">
            Regístrate gratis
          </Link>
        </p>
      </div>
    </div>
  )
}
