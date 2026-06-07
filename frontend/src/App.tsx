import { createBrowserRouter, Navigate, RouterProvider } from "react-router-dom"
import LandingPage from "@/pages/LandingPage"
import LoginPage from "@/pages/LoginPage"
import RegisterPage from "@/pages/RegisterPage"
import Dashboard from "@/pages/Dashboard"
import PrivateRoute from "@/components/PrivateRoute"
import AdminRoute from "@/components/AdminRoute"
import AdminSidebar from "@/components/AdminSidebar"
import MainLayout from "@/layouts/MainLayout"
import AdminDashboard from "@/pages/admin/AdminDashboard"
import AdminUsers from "@/pages/admin/AdminUsers"
import AdminQuestions from "@/pages/admin/AdminQuestions"
import AdminCategories from "@/pages/admin/AdminCategories"
import AdminAchievements from "@/pages/admin/AdminAchievements"
import AdminDifficultyLevels from "@/pages/admin/AdminDifficultyLevels"
import AdminInterviewTypes from "@/pages/admin/AdminInterviewTypes"
import AdminEvaluationCriteria from "@/pages/admin/AdminEvaluationCriteria"
import AdminAuditLogs from "@/pages/admin/AdminAuditLogs"
import { useAuth } from "@/context/AuthContext"

function PrivateDashboard() {
  const { user } = useAuth()
  return (
    <PrivateRoute fallback={<Navigate to="/login" replace />}>
      {user ? <Dashboard /> : null}
    </PrivateRoute>
  )
}

function AdminLayout({ children }: { children: React.ReactNode }) {
  return (
    <MainLayout sidebar={<AdminSidebar />} content={children} />
  )
}

function ProtectedAdminRoute({ children }: { children: React.ReactNode }) {
  return (
    <AdminRoute fallback={<Navigate to="/login" replace />}>
      <AdminLayout>{children}</AdminLayout>
    </AdminRoute>
  )
}

const router = createBrowserRouter([
  { path: "/", element: <LandingPage /> },
  { path: "/login", element: <LoginPage /> },
  { path: "/register", element: <RegisterPage /> },
  { path: "/dashboard", element: <PrivateDashboard /> },
  { path: "/admin", element: <ProtectedAdminRoute><AdminDashboard /></ProtectedAdminRoute> },
  { path: "/admin/users", element: <ProtectedAdminRoute><AdminUsers /></ProtectedAdminRoute> },
  { path: "/admin/questions", element: <ProtectedAdminRoute><AdminQuestions /></ProtectedAdminRoute> },
  { path: "/admin/categories", element: <ProtectedAdminRoute><AdminCategories /></ProtectedAdminRoute> },
  { path: "/admin/achievements", element: <ProtectedAdminRoute><AdminAchievements /></ProtectedAdminRoute> },
  { path: "/admin/difficulty-levels", element: <ProtectedAdminRoute><AdminDifficultyLevels /></ProtectedAdminRoute> },
  { path: "/admin/interview-types", element: <ProtectedAdminRoute><AdminInterviewTypes /></ProtectedAdminRoute> },
  { path: "/admin/evaluation-criteria", element: <ProtectedAdminRoute><AdminEvaluationCriteria /></ProtectedAdminRoute> },
  { path: "/admin/audit-logs", element: <ProtectedAdminRoute><AdminAuditLogs /></ProtectedAdminRoute> },
])

export default function App() {
  return <RouterProvider router={router} />
}
