import { createKeys } from "./keys"

export const categoriesKeys = {
  ...createKeys("categories"),
  bySlug: (slug: string) => ["categories", "by-slug", slug] as const,
  list: (activeOnly?: boolean) =>
    activeOnly
      ? (["categories", "list", { activeOnly }] as const)
      : (["categories", "list"] as const),
}
