export function createKeys(resource: string) {
  return {
    all: [resource] as const,
    lists: () => [resource, "list"] as const,
    list: (filters?: Record<string, unknown>) =>
      filters ? ([resource, "list", filters] as const) : ([resource, "list"] as const),
    details: () => [resource, "detail"] as const,
    detail: (id: string) => [resource, "detail", id] as const,
  }
}
