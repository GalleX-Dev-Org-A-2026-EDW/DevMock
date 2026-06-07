import { createKeys } from "./keys"

export const adminKeys = {
  ...createKeys("admin"),
  dashboard: () => ["admin", "dashboard"] as const,
}
