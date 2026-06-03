import { createContext, useContext, useState, type ReactNode, useCallback } from "react"
import { getToken, setToken, getUsername, setUsername, clearAuth } from "@/api/http"

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

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const token = getToken()
    const username = getUsername()
    return token && username ? { token, username } : null
  })

  const login = useCallback((token: string, username: string) => {
    setToken(token)
    setUsername(username)
    setUser({ token, username })
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
