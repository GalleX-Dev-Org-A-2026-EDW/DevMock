import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { answerOptionsApi } from "./answer-options"
import type { AnswerOption, CreateAnswerOptionDto, UpdateAnswerOptionDto } from "./answer-options"
import { answerOptionsKeys } from "./answer-options.keys"

export function useAnswerOptions() {
  return useQuery({
    queryKey: answerOptionsKeys.lists(),
    queryFn: () => answerOptionsApi.list(),
  })
}

export function useAnswerOption(id: string) {
  return useQuery({
    queryKey: answerOptionsKeys.detail(id),
    queryFn: () => answerOptionsApi.getById(id),
    enabled: !!id,
  })
}

export function useCreateAnswerOption() {
  const queryClient = useQueryClient()
  return useMutation<AnswerOption, Error, CreateAnswerOptionDto>({
    mutationFn: (dto) => answerOptionsApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: answerOptionsKeys.lists() })
    },
  })
}

export function useUpdateAnswerOption() {
  const queryClient = useQueryClient()
  return useMutation<AnswerOption, Error, { id: string; dto: UpdateAnswerOptionDto }>({
    mutationFn: ({ id, dto }) => answerOptionsApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: answerOptionsKeys.lists() })
      queryClient.invalidateQueries({ queryKey: answerOptionsKeys.detail(data.id) })
    },
  })
}

export function useDeleteAnswerOption() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => answerOptionsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: answerOptionsKeys.lists() })
    },
  })
}
