import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { evaluationCriteriaApi } from "./evaluation-criteria"
import type { EvaluationCriterion, CreateEvaluationCriterionDto, UpdateEvaluationCriterionDto } from "./evaluation-criteria"
import { evaluationCriteriaKeys } from "./evaluation-criteria.keys"

export function useEvaluationCriteria(activeOnly?: boolean) {
  return useQuery({
    queryKey: evaluationCriteriaKeys.list(activeOnly),
    queryFn: () => evaluationCriteriaApi.list(activeOnly),
  })
}

export function useEvaluationCriterion(id: string) {
  return useQuery({
    queryKey: evaluationCriteriaKeys.detail(id),
    queryFn: () => evaluationCriteriaApi.getById(id),
    enabled: !!id,
  })
}

export function useEvaluationCriterionBySlug(slug: string) {
  return useQuery({
    queryKey: evaluationCriteriaKeys.bySlug(slug),
    queryFn: () => evaluationCriteriaApi.getBySlug(slug),
    enabled: !!slug,
  })
}

export function useCreateEvaluationCriterion() {
  const queryClient = useQueryClient()
  return useMutation<EvaluationCriterion, Error, CreateEvaluationCriterionDto>({
    mutationFn: (dto) => evaluationCriteriaApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: evaluationCriteriaKeys.lists() })
    },
  })
}

export function useUpdateEvaluationCriterion() {
  const queryClient = useQueryClient()
  return useMutation<EvaluationCriterion, Error, { id: string; dto: UpdateEvaluationCriterionDto }>({
    mutationFn: ({ id, dto }) => evaluationCriteriaApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: evaluationCriteriaKeys.lists() })
      queryClient.invalidateQueries({ queryKey: evaluationCriteriaKeys.detail(data.id) })
    },
  })
}

export function useDeleteEvaluationCriterion() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => evaluationCriteriaApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: evaluationCriteriaKeys.lists() })
    },
  })
}
