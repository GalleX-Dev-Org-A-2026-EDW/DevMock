const TOKEN_KEY = "token"
const USERNAME_KEY = "username"
const ROLE_KEY = "role"

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

export function getUsername(): string | null {
  return localStorage.getItem(USERNAME_KEY)
}

export function setUsername(username: string): void {
  localStorage.setItem(USERNAME_KEY, username)
}

export function removeUsername(): void {
  localStorage.removeItem(USERNAME_KEY)
}

export function getRole(): string | null {
  return localStorage.getItem(ROLE_KEY)
}

export function setRole(role: string): void {
  localStorage.setItem(ROLE_KEY, role)
}

export function removeRole(): void {
  localStorage.removeItem(ROLE_KEY)
}

export function clearAuth(): void {
  removeToken()
  removeUsername()
  removeRole()
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
  return rawFetch<T>(path, options)
}

/**
 * Like `http<T>` but asserts the response is never 204, returning `Promise<T>`
 * instead of `Promise<T | undefined>`. Use for POST/PUT/PATCH/DELETE endpoints
 * that always return a body, or when the caller knows a 204 won't occur.
 */
export async function httpRequired<T>(
  path: string,
  options?: RequestInit,
): Promise<T> {
  return (await rawFetch<T>(path, options))!
}

async function rawFetch<T>(
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

  if (res.status === 401) {
    clearAuth()
    window.location.href = "/login"
    throw new Error("Sesión expirada")
  }

  if (!res.ok) {
    const body = await res.text().catch(() => null)
    const parsed = body ? tryParseJson(body) : null
    const message = parsed?.message ?? body ?? `Error HTTP ${res.status}`
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
