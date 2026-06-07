import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { questionsApi } from "./questions"
import type { Question, CreateQuestionDto, UpdateQuestionDto } from "./questions"
import { questionsKeys } from "./questions.keys"

export function useQuestions() {
  return useQuery({
    queryKey: questionsKeys.lists(),
    queryFn: () => questionsApi.list(),
  })
}

export function useQuestion(id: string) {
  return useQuery({
    queryKey: questionsKeys.detail(id),
    queryFn: () => questionsApi.getById(id),
    enabled: !!id,
  })
}

export function useCreateQuestion() {
  const queryClient = useQueryClient()
  return useMutation<Question, Error, CreateQuestionDto>({
    mutationFn: (dto) => questionsApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: questionsKeys.lists() })
    },
  })
}

export function useUpdateQuestion() {
  const queryClient = useQueryClient()
  return useMutation<Question, Error, { id: string; dto: UpdateQuestionDto }>({
    mutationFn: ({ id, dto }) => questionsApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: questionsKeys.lists() })
      queryClient.invalidateQueries({ queryKey: questionsKeys.detail(data.id) })
    },
  })
}

export function useDeleteQuestion() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => questionsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: questionsKeys.lists() })
    },
  })
}
