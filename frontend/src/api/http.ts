const TOKEN_KEY = "auth_token"

export const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080"

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

export function clearAuth(): void {
  removeToken()
}

export class ApiError extends Error {
  status: number
  code: string | null
  fields: Record<string, string> | null

  constructor(status: number, code: string | null, message: string, fields?: Record<string, string>) {
    super(message)
    this.name = "ApiError"
    this.status = status
    this.code = code
    this.fields = fields ?? null
  }
}

export async function http<T>(
  path: string,
  options?: RequestInit,
): Promise<T | undefined> {
  const url = `${API_URL.replace(/\/+$/, "")}/${path.replace(/^\/+/, "")}`

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options?.headers as Record<string, string> | undefined),
  }

  const token = getToken()
  if (token) {
    headers["Authorization"] = `Bearer ${token}`
  }

  const res = await fetch(url, { ...options, headers })

  if (!res.ok) {
    const body = await res.text().catch(() => null)
    const parsed = body ? tryParseJson(body) : null
    const message = parsed?.message ?? body ?? `HTTP ${res.status}`
    throw new ApiError(
      res.status,
      typeof parsed?.code === "string" ? parsed.code : null,
      message,
      parsed?.fields as Record<string, string> | undefined,
    )
  }

  if (res.status === 204) return undefined

  return res.json() as Promise<T>
}

interface ErrorResponse {
  code?: string
  message?: string
  fields?: Record<string, string>
}

function tryParseJson(text: string): ErrorResponse | null {
  try {
    return JSON.parse(text)
  } catch {
    return null
  }
}
