import { createContext, useContext, useState, type ReactNode, useCallback } from "react"
import { getToken, setToken, getUsername, setUsername, clearAuth, getRole, setRole } from "@/api/http"
import type { UserRole } from "@/api/enums"

interface AuthUser {
  token: string
  username: string
  role: UserRole
}

interface AuthContextType {
  user: AuthUser | null
  login: (token: string, username: string, role: UserRole) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const token = getToken()
    const username = getUsername()
    const role = getRole() as UserRole | null
    return token && username && role ? { token, username, role } : null
  })

  const login = useCallback((token: string, username: string, role: UserRole) => {
    setToken(token)
    setUsername(username)
    setRole(role)
    setUser({ token, username, role })
  }, [])

  const logout = useCallback(() => {
    clearAuth()
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
