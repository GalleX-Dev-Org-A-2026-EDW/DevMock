import { createKeys } from "./keys"

export const usersKeys = {
  ...createKeys("users"),
  byEmail: (email: string) => ["users", "by-email", email] as const,
}
