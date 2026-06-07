import { API_URL, ApiError, setToken, setRole, clearAuth } from "./http"

export type UserRole = "STUDENT" | "PROFESSIONAL" | "ADMIN"

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  fullName: string
  role: UserRole
  avatarUrl?: string
  professionalExperienceYears?: number
  currentLevelId?: string
}

export interface AuthResponse {
  token: string
  email: string
  fullName: string
  role: UserRole
}

async function authFetch<T>(path: string, options: RequestInit): Promise<T> {
  const url = `${API_URL.replace(/\/+$/, "")}/${path.replace(/^\/+/, "")}`
  const res = await fetch(url, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.headers as Record<string, string> | undefined),
    },
  })
  if (!res.ok) {
    const body = await res.text().catch(() => null)
    const parsed = body ? tryParse(body) : null
    throw new ApiError(
      res.status,
      parsed?.code ?? null,
      parsed?.message ?? body ?? `HTTP ${res.status}`,
      parsed?.fields as Record<string, string> | undefined,
    )
  }
  return res.json() as Promise<T>
}

function tryParse(text: string): { code?: string; message?: string; fields?: Record<string, string> } | null {
  try { return JSON.parse(text) } catch { return null }
}

export const authApi = {
  login: async (dto: LoginRequest) => {
    clearAuth()
    const res = await authFetch<AuthResponse>("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(dto),
    })
    setToken(res.token)
    setRole(res.role)
    return res
  },

  register: async (dto: RegisterRequest) => {
    clearAuth()
    const res = await authFetch<AuthResponse>("/api/auth/register", {
      method: "POST",
      body: JSON.stringify(dto),
    })
    setToken(res.token)
    setRole(res.role)
    return res
  },
}
