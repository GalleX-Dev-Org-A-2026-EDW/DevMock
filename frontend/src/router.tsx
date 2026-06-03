import { createBrowserRouter, Navigate } from "react-router-dom"
import MainLayout from "@/layouts/MainLayout"
import Dashboard from "@/pages/Dashboard"
import Demo from "@/pages/Demo"
import Login from "@/pages/Login"

export const router = createBrowserRouter([
  {
    path: "/",
    element: <MainLayout />,
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: "dashboard", element: <Dashboard /> },
      { path: "demo", element: <Demo /> },
    ],
  },
  {
    path: "/login",
    element: <Login />,
  },
])
