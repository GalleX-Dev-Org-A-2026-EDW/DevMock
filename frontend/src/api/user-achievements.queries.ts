import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { userAchievementsApi } from "./user-achievements"
import type { UserAchievement, CreateUserAchievementDto, UpdateUserAchievementDto } from "./user-achievements"
import { userAchievementsKeys } from "./user-achievements.keys"

export function useUserAchievements() {
  return useQuery({
    queryKey: userAchievementsKeys.lists(),
    queryFn: () => userAchievementsApi.list(),
  })
}

export function useUserAchievement(id: string) {
  return useQuery({
    queryKey: userAchievementsKeys.detail(id),
    queryFn: () => userAchievementsApi.getById(id),
    enabled: !!id,
  })
}

export function useCreateUserAchievement() {
  const queryClient = useQueryClient()
  return useMutation<UserAchievement, Error, CreateUserAchievementDto>({
    mutationFn: (dto) => userAchievementsApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: userAchievementsKeys.lists() })
    },
  })
}

export function useUpdateUserAchievement() {
  const queryClient = useQueryClient()
  return useMutation<UserAchievement, Error, { id: string; dto: UpdateUserAchievementDto }>({
    mutationFn: ({ id, dto }) => userAchievementsApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: userAchievementsKeys.lists() })
      queryClient.invalidateQueries({ queryKey: userAchievementsKeys.detail(data.id) })
    },
  })
}

export function useDeleteUserAchievement() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => userAchievementsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: userAchievementsKeys.lists() })
    },
  })
}
