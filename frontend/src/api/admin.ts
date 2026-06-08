import { http } from "./http"
import type { User } from "./users"

export interface AdminDashboardStats {
  totalUsers: number
  activeUsers: number
  studentsCount: number
  professionalsCount: number
  adminsCount: number
  totalSessions: number
  sessionsToday: number
  completedSessions: number
  inProgressSessions: number
  totalQuestions: number
  activeQuestions: number
  totalCategories: number
  activeCategories: number
  totalAchievements: number
  recentUsers: User[]
}

export const adminApi = {
  getDashboard: () => http<AdminDashboardStats>("/api/admin/dashboard"),
}
