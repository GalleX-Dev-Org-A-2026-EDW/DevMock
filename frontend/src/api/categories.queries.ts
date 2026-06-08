import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { categoriesApi } from "./categories"
import type { Category, CreateCategoryDto, UpdateCategoryDto } from "./categories"
import { categoriesKeys } from "./categories.keys"

export function useCategories(activeOnly?: boolean) {
  return useQuery({
    queryKey: categoriesKeys.list(activeOnly),
    queryFn: () => categoriesApi.list(activeOnly),
  })
}

export function useCategory(id: string) {
  return useQuery({
    queryKey: categoriesKeys.detail(id),
    queryFn: () => categoriesApi.getById(id),
    enabled: !!id,
  })
}

export function useCategoryBySlug(slug: string) {
  return useQuery({
    queryKey: categoriesKeys.bySlug(slug),
    queryFn: () => categoriesApi.getBySlug(slug),
    enabled: !!slug,
  })
}

export function useCreateCategory() {
  const queryClient = useQueryClient()
  return useMutation<Category, Error, CreateCategoryDto>({
    mutationFn: (dto) => categoriesApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: categoriesKeys.lists() })
    },
  })
}

export function useUpdateCategory() {
  const queryClient = useQueryClient()
  return useMutation<Category, Error, { id: string; dto: UpdateCategoryDto }>({
    mutationFn: ({ id, dto }) => categoriesApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: categoriesKeys.lists() })
      queryClient.invalidateQueries({ queryKey: categoriesKeys.detail(data.id) })
    },
  })
}

export function useDeleteCategory() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => categoriesApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: categoriesKeys.lists() })
    },
  })
}
