import { createBrowserRouter, Navigate, RouterProvider } from "react-router-dom"
import LandingPage from "@/pages/LandingPage"
import LoginPage from "@/pages/LoginPage"
import RegisterPage from "@/pages/RegisterPage"
import Dashboard from "@/pages/Dashboard"
import PrivateRoute from "@/components/PrivateRoute"
import { useAuth } from "@/context/AuthContext"

function PrivateDashboard() {
  const { user } = useAuth()
  return (
    <PrivateRoute fallback={<Navigate to="/login" replace />}>
      {user ? <Dashboard /> : null}
    </PrivateRoute>
  )
}

const router = createBrowserRouter([
  { path: "/", element: <LandingPage /> },
  { path: "/login", element: <LoginPage /> },
  { path: "/register", element: <RegisterPage /> },
  { path: "/dashboard", element: <PrivateDashboard /> },
])

export default function App() {
  return <RouterProvider router={router} />
}
