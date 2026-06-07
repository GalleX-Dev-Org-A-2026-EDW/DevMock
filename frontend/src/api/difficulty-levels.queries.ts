import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { difficultyLevelsApi } from "./difficulty-levels"
import type { DifficultyLevel, CreateDifficultyLevelDto, UpdateDifficultyLevelDto } from "./difficulty-levels"
import { difficultyLevelsKeys } from "./difficulty-levels.keys"

export function useDifficultyLevels() {
  return useQuery({
    queryKey: difficultyLevelsKeys.lists(),
    queryFn: () => difficultyLevelsApi.list(),
  })
}

export function useDifficultyLevel(id: string) {
  return useQuery({
    queryKey: difficultyLevelsKeys.detail(id),
    queryFn: () => difficultyLevelsApi.getById(id),
    enabled: !!id,
  })
}

export function useDifficultyLevelBySlug(slug: string) {
  return useQuery({
    queryKey: difficultyLevelsKeys.bySlug(slug),
    queryFn: () => difficultyLevelsApi.getBySlug(slug),
    enabled: !!slug,
  })
}

export function useCreateDifficultyLevel() {
  const queryClient = useQueryClient()
  return useMutation<DifficultyLevel, Error, CreateDifficultyLevelDto>({
    mutationFn: (dto) => difficultyLevelsApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: difficultyLevelsKeys.lists() })
    },
  })
}

export function useUpdateDifficultyLevel() {
  const queryClient = useQueryClient()
  return useMutation<DifficultyLevel, Error, { id: string; dto: UpdateDifficultyLevelDto }>({
    mutationFn: ({ id, dto }) => difficultyLevelsApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: difficultyLevelsKeys.lists() })
      queryClient.invalidateQueries({ queryKey: difficultyLevelsKeys.detail(data.id) })
    },
  })
}

export function useDeleteDifficultyLevel() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => difficultyLevelsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: difficultyLevelsKeys.lists() })
    },
  })
}
