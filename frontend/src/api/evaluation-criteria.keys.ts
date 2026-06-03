import { createKeys } from "./keys"

export const evaluationCriteriaKeys = {
  ...createKeys("evaluation-criteria"),
  bySlug: (slug: string) => ["evaluation-criteria", "by-slug", slug] as const,
  list: (activeOnly?: boolean) =>
    activeOnly
      ? (["evaluation-criteria", "list", { activeOnly }] as const)
      : (["evaluation-criteria", "list"] as const),
}
