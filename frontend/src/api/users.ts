import { http } from "./http"
import type { UserRole } from "./enums"

export interface User {
  id: string
  email: string
  fullName: string
  avatarUrl: string | null
  role: UserRole
  professionalExperienceYears: number | null
  isActive: boolean
  isVerified: boolean
  emailVerifiedAt: string | null
  lastLoginAt: string | null
  currentLevelId: string | null
  currentLevelName: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateUserDto {
  email: string
  password: string
  fullName: string
  role: UserRole
  avatarUrl?: string
  professionalExperienceYears?: number
  currentLevelId?: string
}

export interface UpdateUserDto {
  fullName?: string
  avatarUrl?: string
  role?: UserRole
  professionalExperienceYears?: number
  isActive?: boolean
  isVerified?: boolean
  password?: string
  currentLevelId?: string
}

export const usersApi = {
  list: () => http<User[]>("/api/users"),

  getById: (id: string) => http<User>(`/api/users/${id}`),

  getByEmail: (email: string) =>
    http<User>(`/api/users/by-email?email=${encodeURIComponent(email)}`),

  create: (dto: CreateUserDto) =>
    http<User>("/api/users", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateUserDto) =>
    http<User>(`/api/users/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    http<void>(`/api/users/${id}`, { method: "DELETE" }),
}
