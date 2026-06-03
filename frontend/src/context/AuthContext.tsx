import { createContext, useContext, useState, type ReactNode, useCallback } from "react"
import { getToken, setToken, removeToken, clearAuth } from "@/api/http"

interface AuthUser {
  token: string
  username: string
}

interface AuthContextType {
  user: AuthUser | null
  login: (token: string, username: string) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

const STORAGE_USERNAME_KEY = "auth_username"

function getStoredUsername(): string | null {
  return localStorage.getItem(STORAGE_USERNAME_KEY)
}

function setStoredUsername(username: string): void {
  localStorage.setItem(STORAGE_USERNAME_KEY, username)
}

function clearStoredUsername(): void {
  localStorage.removeItem(STORAGE_USERNAME_KEY)
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const token = getToken()
    const username = getStoredUsername()
    return token && username ? { token, username } : null
  })

  const login = useCallback((token: string, username: string) => {
    setToken(token)
    setStoredUsername(username)
    setUser({ token, username })
  }, [])

  const logout = useCallback(() => {
    clearAuth()
    clearStoredUsername()
    setUser(null)
  }, [])

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth(): AuthContextType {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error("useAuth must be used within an <AuthProvider>")
  }
  return ctx
}
