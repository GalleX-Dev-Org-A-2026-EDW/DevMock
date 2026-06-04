import { useQuery } from "@tanstack/react-query"
import { adminApi } from "./admin"
import { adminKeys } from "./admin.keys"

export function useAdminDashboard() {
  return useQuery({
    queryKey: adminKeys.dashboard(),
    queryFn: () => adminApi.getDashboard(),
  })
}
