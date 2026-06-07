import { useState, type FormEvent } from "react"
import { Link, useNavigate } from "react-router-dom"
import { ChevronLeft, Mail, Lock, User, AlertCircle, CheckCircle2, Eye, EyeOff } from "lucide-react"
import { authApi } from "@/api/auth"
import { useAuth } from "@/context/AuthContext"
import { ApiError } from "@/api/http"
import type { UserRole } from "@/api/auth"
import devMockIcon from "@/assets/DevMockIcono.png"

interface FieldErrors {
  fullName?: string
  email?: string
  password?: string
  confirmPassword?: string
  terms?: string
}

function extractFieldErrors(err: unknown): FieldErrors {
  if (err instanceof ApiError && err.fields) {
    const map: Record<string, string> = {
      fullName: err.fields.fullName,
      email: err.fields.email,
      password: err.fields.password,
    }
    return Object.fromEntries(Object.entries(map).filter(([, v]) => v))
  }
  return {}
}

function errorMessage(err: unknown): string {
  if (err instanceof ApiError) {
    if (err.code === "EMAIL_ALREADY_EXISTS") return "Este email ya está registrado. Inicia sesión o usa otro email."
    if (err.code === "NAME_ALREADY_EXISTS") return "Este nombre de usuario ya está en uso."
    if (err.code === "VALIDATION_ERROR") return "Corrige los errores en el formulario."
    if (err.status === 409) return err.message
    return err.message
  }
  if (err instanceof TypeError && err.message === "Failed to fetch") {
    return "No se pudo conectar con el servidor. Verifica tu conexión."
  }
  return err instanceof Error ? err.message : "Error inesperado. Intenta de nuevo."
}

const passwordStrength = (p: string) => {
  if (!p) return { strength: 0, label: "", color: "" }
  if (p.length < 6) return { strength: 1, label: "Débil", color: "bg-red-500" }
  if (p.length < 10) return { strength: 2, label: "Media", color: "bg-yellow-500" }
  if (p.length >= 10 && /[A-Z]/.test(p) && /[0-9]/.test(p))
    return { strength: 3, label: "Fuerte", color: "bg-green-500" }
  return { strength: 2, label: "Media", color: "bg-yellow-500" }
}

export default function RegisterPage() {
  const navigate = useNavigate()
  const [name, setName] = useState("")
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirm, setShowConfirm] = useState(false)
  const [role, setRole] = useState<"STUDENT" | "PROFESSIONAL">("STUDENT")
  const [error, setError] = useState("")
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
  const [loading, setLoading] = useState(false)
  const [acceptedTerms, setAcceptedTerms] = useState(false)
  const { login } = useAuth()

  const strength = passwordStrength(password)

  const validate = (): boolean => {
    const fields: FieldErrors = {}
    if (!name.trim()) fields.fullName = "El nombre es requerido."
    if (!email.trim()) fields.email = "El email es requerido."
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) fields.email = "Email inválido"
    if (!password) fields.password = "La contraseña es requerida."
    else if (password.length < 6) fields.password = "La contraseña debe tener al menos 6 caracteres."
    if (!confirmPassword) fields.confirmPassword = "Confirma tu contraseña."
    else if (password !== confirmPassword) fields.confirmPassword = "Las contraseñas no coinciden."
    if (!acceptedTerms) fields.terms = "Debes aceptar los términos y condiciones."
    setFieldErrors(fields)
    return Object.keys(fields).length === 0
  }

  const clearField = (field: keyof FieldErrors) =>
    setFieldErrors((p) => ({ ...p, [field]: undefined }))

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError("")
    if (!validate()) return

    setLoading(true)
    try {
      const res = await authApi.register({
        fullName: name,
        email,
        password,
        role,
      })
      login(res.token, res.fullName, res.role as UserRole)
      navigate("/dashboard")
    } catch (err) {
      setError(errorMessage(err))
      setFieldErrors((prev) => ({ ...prev, ...extractFieldErrors(err) }))
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
            Crear cuenta
          </h1>
          <p className="text-sm text-muted-foreground">
            Únete a miles de desarrolladores mejorando sus habilidades
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
              <label className="block text-sm font-medium text-foreground mb-3">
                ¿Cuál es tu perfil?
              </label>
              <div className="grid grid-cols-2 gap-3">
                <button
                  type="button"
                  onClick={() => setRole("STUDENT")}
                  className={`p-4 rounded-lg border-2 transition-all text-left ${
                    role === "STUDENT"
                      ? "border-primary bg-primary/5"
                      : "border-border hover:border-primary/50"
                  }`}
                >
                  <div className="font-semibold text-sm text-foreground mb-1">Estudiante</div>
                  <div className="text-xs text-muted-foreground">Preparándome para mi primera entrevista</div>
                </button>
                <button
                  type="button"
                  onClick={() => setRole("PROFESSIONAL")}
                  className={`p-4 rounded-lg border-2 transition-all text-left ${
                    role === "PROFESSIONAL"
                      ? "border-primary bg-primary/5"
                      : "border-border hover:border-primary/50"
                  }`}
                >
                  <div className="font-semibold text-sm text-foreground mb-1">Profesional</div>
                  <div className="text-xs text-muted-foreground">Ya tengo experiencia en desarrollo</div>
                </button>
              </div>
            </div>

            <div>
              <label htmlFor="name" className="block text-sm font-medium text-foreground mb-2">
                Nombre completo
              </label>
              <div className="relative">
                <User className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <input
                  id="name"
                  type="text"
                  value={name}
                  onChange={(e) => { setName(e.target.value); clearField("fullName") }}
                  placeholder="Juan Pérez"
                  required
                  className={inputClass(!!fieldErrors.fullName)}
                  autoComplete="name"
                />
              </div>
              {fieldErrors.fullName && (
                <p className="text-xs text-red-500 mt-1">{fieldErrors.fullName}</p>
              )}
            </div>

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
                  onChange={(e) => { setEmail(e.target.value); clearField("email") }}
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
              <label htmlFor="password" className="block text-sm font-medium text-foreground mb-2">
                Contraseña
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => { setPassword(e.target.value); clearField("password") }}
                  placeholder="••••••••"
                  required
                  className={inputClass(!!fieldErrors.password)}
                  autoComplete="new-password"
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
              {password && (
                <div className="mt-2">
                  <div className="flex gap-1 mb-1">
                    {[1, 2, 3].map((level) => (
                      <div
                        key={level}
                        className={`h-1 flex-1 rounded-full transition-all ${
                          level <= strength.strength ? strength.color : "bg-border"
                        }`}
                      />
                    ))}
                  </div>
                  <p className="text-xs text-muted-foreground">
                    Seguridad: <span className="font-medium">{strength.label}</span>
                  </p>
                </div>
              )}
            </div>

            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-foreground mb-2">
                Confirmar contraseña
              </label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground" />
                <input
                  id="confirmPassword"
                  type={showConfirm ? "text" : "password"}
                  value={confirmPassword}
                  onChange={(e) => { setConfirmPassword(e.target.value); clearField("confirmPassword") }}
                  placeholder="••••••••"
                  required
                  className={inputClass(!!fieldErrors.confirmPassword)}
                  autoComplete="new-password"
                />
                <button
                  type="button"
                  onClick={() => setShowConfirm((p) => !p)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                  tabIndex={-1}
                >
                  {showConfirm ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
                {confirmPassword && password === confirmPassword && !fieldErrors.confirmPassword && (
                  <CheckCircle2 className="absolute right-10 top-1/2 -translate-y-1/2 h-5 w-5 text-green-500" />
                )}
              </div>
              {fieldErrors.confirmPassword && (
                <p className="text-xs text-red-500 mt-1">{fieldErrors.confirmPassword}</p>
              )}
            </div>

            <div className="flex items-start gap-3">
              <input
                id="terms"
                type="checkbox"
                checked={acceptedTerms}
                onChange={(e) => { setAcceptedTerms(e.target.checked); clearField("terms") }}
                className="h-4 w-4 rounded border-border text-primary focus:ring-2 focus:ring-primary focus:ring-offset-0 mt-0.5"
              />
              <label htmlFor="terms" className={`text-sm select-none cursor-pointer ${fieldErrors.terms ? "text-red-500" : "text-muted-foreground"}`}>
                Acepto los{" "}
                <a href="#" className="text-primary hover:text-primary/80 font-medium">
                  términos y condiciones
                </a>{" "}
                y la{" "}
                <a href="#" className="text-primary hover:text-primary/80 font-medium">
                  política de privacidad
                </a>
              </label>
            </div>
            {fieldErrors.terms && (
              <p className="text-xs text-red-500 -mt-3">{fieldErrors.terms}</p>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full px-4 py-3 bg-primary hover:bg-primary/90 text-primary-foreground rounded-lg font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed shadow-sm hover:shadow-md"
            >
              {loading ? "Creando cuenta..." : "Crear cuenta"}
            </button>

            {error && !!Object.keys(fieldErrors).length && (
              <p className="text-xs text-red-500 text-center">{error}</p>
            )}
          </form>
        </div>

        <p className="text-center text-sm text-muted-foreground mt-6">
          ¿Ya tienes una cuenta?{" "}
          <Link to="/login" className="font-medium text-primary hover:text-primary/80 transition-colors">
            Inicia sesión
          </Link>
        </p>
      </div>
    </div>
  )
}
