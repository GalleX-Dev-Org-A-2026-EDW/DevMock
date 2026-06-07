import { createKeys } from "./keys"

export const difficultyLevelsKeys = {
  ...createKeys("difficulty-levels"),
  bySlug: (slug: string) => ["difficulty-levels", "by-slug", slug] as const,
}
