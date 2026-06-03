import { http } from "./http"

export interface Category {
  id: string
  name: string
  slug: string
  description: string | null
  icon: string | null
  displayOrder: number | null
  isActive: boolean
  parentId: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateCategoryDto {
  name: string
  slug: string
  description?: string
  icon?: string
  displayOrder?: number
  parentId?: string
}

export interface UpdateCategoryDto {
  name?: string
  slug?: string
  description?: string
  icon?: string
  displayOrder?: number
  parentId?: string
  isActive?: boolean
}

export const categoriesApi = {
  list: (activeOnly?: boolean) =>
    http<Category[]>(
      activeOnly
        ? "/api/categories?activeOnly=true"
        : "/api/categories",
    ),

  getById: (id: string) => http<Category>(`/api/categories/${id}`),

  getBySlug: (slug: string) =>
    http<Category>(`/api/categories/by-slug/${encodeURIComponent(slug)}`),

  create: (dto: CreateCategoryDto) =>
    http<Category>("/api/categories", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateCategoryDto) =>
    http<Category>(`/api/categories/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    http<void>(`/api/categories/${id}`, { method: "DELETE" }),
}
