import type { ReactNode } from "react"
import { useAuth } from "@/context/AuthContext"

interface Props {
  children: ReactNode
  fallback: ReactNode
}

export default function AdminRoute({ children, fallback }: Props) {
  const { user } = useAuth()
  if (!user) return <>{fallback}</>
  if (user.role !== "ADMIN") return <>{fallback}</>
  return <>{children}</>
}
