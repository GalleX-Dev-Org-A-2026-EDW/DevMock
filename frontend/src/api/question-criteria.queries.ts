import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { questionCriteriaApi } from "./question-criteria"
import type { QuestionCriterion, CreateQuestionCriterionDto, UpdateQuestionCriterionDto } from "./question-criteria"
import { questionCriteriaKeys } from "./question-criteria.keys"

export function useQuestionCriteria() {
  return useQuery({
    queryKey: questionCriteriaKeys.lists(),
    queryFn: () => questionCriteriaApi.list(),
  })
}

export function useQuestionCriterion(id: string) {
  return useQuery({
    queryKey: questionCriteriaKeys.detail(id),
    queryFn: () => questionCriteriaApi.getById(id),
    enabled: !!id,
  })
}

export function useCreateQuestionCriterion() {
  const queryClient = useQueryClient()
  return useMutation<QuestionCriterion, Error, CreateQuestionCriterionDto>({
    mutationFn: (dto) => questionCriteriaApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: questionCriteriaKeys.lists() })
    },
  })
}

export function useUpdateQuestionCriterion() {
  const queryClient = useQueryClient()
  return useMutation<QuestionCriterion, Error, { id: string; dto: UpdateQuestionCriterionDto }>({
    mutationFn: ({ id, dto }) => questionCriteriaApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: questionCriteriaKeys.lists() })
      queryClient.invalidateQueries({ queryKey: questionCriteriaKeys.detail(data.id) })
    },
  })
}

export function useDeleteQuestionCriterion() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => questionCriteriaApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: questionCriteriaKeys.lists() })
    },
  })
}
