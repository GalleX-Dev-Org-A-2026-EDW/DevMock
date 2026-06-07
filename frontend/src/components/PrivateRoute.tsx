import type { ReactNode } from "react"
import { useAuth } from "@/context/AuthContext"

interface Props {
  children: ReactNode
  fallback: ReactNode
}

export default function PrivateRoute({ children, fallback }: Props) {
  const { user } = useAuth()
  return user ? <>{children}</> : <>{fallback}</>
}
