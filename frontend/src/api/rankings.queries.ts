import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { rankingsApi } from "./rankings"
import type { Ranking, CreateRankingDto, UpdateRankingDto } from "./rankings"
import { rankingsKeys } from "./rankings.keys"

export function useRankings() {
  return useQuery({
    queryKey: rankingsKeys.lists(),
    queryFn: () => rankingsApi.list(),
  })
}

export function useRanking(id: string) {
  return useQuery({
    queryKey: rankingsKeys.detail(id),
    queryFn: () => rankingsApi.getById(id),
    enabled: !!id,
  })
}

export function useCreateRanking() {
  const queryClient = useQueryClient()
  return useMutation<Ranking, Error, CreateRankingDto>({
    mutationFn: (dto) => rankingsApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: rankingsKeys.lists() })
    },
  })
}

export function useUpdateRanking() {
  const queryClient = useQueryClient()
  return useMutation<Ranking, Error, { id: string; dto: UpdateRankingDto }>({
    mutationFn: ({ id, dto }) => rankingsApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: rankingsKeys.lists() })
      queryClient.invalidateQueries({ queryKey: rankingsKeys.detail(data.id) })
    },
  })
}

export function useDeleteRanking() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => rankingsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: rankingsKeys.lists() })
    },
  })
}
