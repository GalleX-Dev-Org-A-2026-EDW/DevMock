import { useState, type FormEvent } from "react"
import { authApi } from "@/api/auth"
import { useAuth } from "@/context/AuthContext"

interface Props {
  onSuccess?: () => void
}

interface Errors {
  email?: string
  password?: string
  server?: string
}

export default function LoginPage({ onSuccess }: Props) {
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const [errors, setErrors] = useState<Errors>({})
  const [loading, setLoading] = useState(false)

  const { login } = useAuth()

  const validate = (): Errors => {
    const e: Errors = {}
    if (!email.trim()) e.email = "El usuario es obligatorio"
    if (!password) e.password = "La contraseña es obligatoria"
    else if (password.length < 6) e.password = "Mínimo 6 caracteres"
    return e
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setErrors({})
    const validation = validate()
    if (validation.email || validation.password) {
      setErrors(validation)
      return
    }
    setLoading(true)
    try {
      const res = await authApi.login({ email, password })
      login(res.token, res.fullName)
      onSuccess?.()
    } catch (err) {
      const message =
        err instanceof Error ? err.message : "Error inesperado"
      setErrors({ server: message })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-100 px-4">
      <div className="w-full max-w-sm rounded-2xl bg-white p-8 shadow-lg">
        <h1 className="mb-6 text-2xl font-bold text-slate-900">Iniciar sesión</h1>

        {errors.server && (
          <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {errors.server}
          </div>
        )}

        <form onSubmit={handleSubmit} noValidate className="flex flex-col gap-5">
          <div className="flex flex-col gap-1.5">
            <label htmlFor="email" className="text-sm font-medium text-slate-700">
              Usuario
            </label>
            <input
              id="email"
              type="text"
              autoComplete="username"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className={`rounded-lg border px-3 py-2 text-sm outline-none transition focus:ring-2 focus:ring-blue-500 ${
                errors.email ? "border-red-300" : "border-slate-300"
              }`}
            />
            {errors.email && (
              <p className="text-xs text-red-600">{errors.email}</p>
            )}
          </div>

          <div className="flex flex-col gap-1.5">
            <label htmlFor="password" className="text-sm font-medium text-slate-700">
              Contraseña
            </label>
            <input
              id="password"
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className={`rounded-lg border px-3 py-2 text-sm outline-none transition focus:ring-2 focus:ring-blue-500 ${
                errors.password ? "border-red-300" : "border-slate-300"
              }`}
            />
            {errors.password && (
              <p className="text-xs text-red-600">{errors.password}</p>
            )}
          </div>

          <button
            type="submit"
            disabled={loading}
            className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:opacity-50"
          >
            {loading ? "Ingresando…" : "Ingresar"}
          </button>
        </form>
      </div>
    </div>
  )
}
