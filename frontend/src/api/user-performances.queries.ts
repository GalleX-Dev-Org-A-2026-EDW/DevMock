import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { userPerformancesApi } from "./user-performances"
import type { UserPerformance, CreateUserPerformanceDto, UpdateUserPerformanceDto } from "./user-performances"
import { userPerformancesKeys } from "./user-performances.keys"

export function useUserPerformances() {
  return useQuery({
    queryKey: userPerformancesKeys.lists(),
    queryFn: () => userPerformancesApi.list(),
  })
}

export function useUserPerformance(id: string) {
  return useQuery({
    queryKey: userPerformancesKeys.detail(id),
    queryFn: () => userPerformancesApi.getById(id),
    enabled: !!id,
  })
}

export function useCreateUserPerformance() {
  const queryClient = useQueryClient()
  return useMutation<UserPerformance, Error, CreateUserPerformanceDto>({
    mutationFn: (dto) => userPerformancesApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: userPerformancesKeys.lists() })
    },
  })
}

export function useUpdateUserPerformance() {
  const queryClient = useQueryClient()
  return useMutation<UserPerformance, Error, { id: string; dto: UpdateUserPerformanceDto }>({
    mutationFn: ({ id, dto }) => userPerformancesApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: userPerformancesKeys.lists() })
      queryClient.invalidateQueries({ queryKey: userPerformancesKeys.detail(data.id) })
    },
  })
}

export function useDeleteUserPerformance() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => userPerformancesApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: userPerformancesKeys.lists() })
    },
  })
}
