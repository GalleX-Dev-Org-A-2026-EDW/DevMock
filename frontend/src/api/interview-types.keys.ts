import { createKeys } from "./keys"

export const interviewTypesKeys = {
  ...createKeys("interview-types"),
  bySlug: (slug: string) => ["interview-types", "by-slug", slug] as const,
  list: (activeOnly?: boolean) =>
    activeOnly
      ? (["interview-types", "list", { activeOnly }] as const)
      : (["interview-types", "list"] as const),
}
