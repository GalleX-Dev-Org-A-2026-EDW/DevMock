import { http, setToken } from "./http"

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
}

export const authApi = {
  login: async (dto: LoginRequest) => {
    const res = await http<AuthResponse>("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(dto),
    })
    if (res) setToken(res.token)
    return res!
  },

  register: async (dto: RegisterRequest) => {
    const res = await http<AuthResponse>("/api/auth/register", {
      method: "POST",
      body: JSON.stringify(dto),
    })
    if (res) setToken(res.token)
    return res!
  },
}
