export const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080"

export async function http<T>(path: string, options?: RequestInit): Promise<T | undefined> {
  const url = `${API_URL.replace(/\/+$/, "")}/${path.replace(/^\/+/, "")}`

  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...options?.headers,
  }

  const res = await fetch(url, { ...options, headers })

  if (!res.ok) {
    const text = (await res.text().catch(() => null)) || `HTTP ${res.status}`
    throw new Error(text)
  }

  if (res.status === 204) {
    return undefined
  }

  return res.json() as Promise<T>
}
