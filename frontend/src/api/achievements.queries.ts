import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { achievementsApi } from "./achievements"
import type { Achievement, CreateAchievementDto, UpdateAchievementDto } from "./achievements"
import { achievementsKeys } from "./achievements.keys"

export function useAchievements() {
  return useQuery({
    queryKey: achievementsKeys.lists(),
    queryFn: () => achievementsApi.list(),
  })
}

export function useAchievement(id: string) {
  return useQuery({
    queryKey: achievementsKeys.detail(id),
    queryFn: () => achievementsApi.getById(id),
    enabled: !!id,
  })
}

export function useCreateAchievement() {
  const queryClient = useQueryClient()
  return useMutation<Achievement, Error, CreateAchievementDto>({
    mutationFn: (dto) => achievementsApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: achievementsKeys.lists() })
    },
  })
}

export function useUpdateAchievement() {
  const queryClient = useQueryClient()
  return useMutation<Achievement, Error, { id: string; dto: UpdateAchievementDto }>({
    mutationFn: ({ id, dto }) => achievementsApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: achievementsKeys.lists() })
      queryClient.invalidateQueries({ queryKey: achievementsKeys.detail(data.id) })
    },
  })
}

export function useDeleteAchievement() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => achievementsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: achievementsKeys.lists() })
    },
  })
}
