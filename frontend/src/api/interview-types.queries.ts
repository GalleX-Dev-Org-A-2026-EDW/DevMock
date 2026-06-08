import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { interviewTypesApi } from "./interview-types"
import type { InterviewType, CreateInterviewTypeDto, UpdateInterviewTypeDto } from "./interview-types"
import { interviewTypesKeys } from "./interview-types.keys"

export function useInterviewTypes(activeOnly?: boolean) {
  return useQuery({
    queryKey: interviewTypesKeys.list(activeOnly),
    queryFn: () => interviewTypesApi.list(activeOnly),
  })
}

export function useInterviewType(id: string) {
  return useQuery({
    queryKey: interviewTypesKeys.detail(id),
    queryFn: () => interviewTypesApi.getById(id),
    enabled: !!id,
  })
}

export function useInterviewTypeBySlug(slug: string) {
  return useQuery({
    queryKey: interviewTypesKeys.bySlug(slug),
    queryFn: () => interviewTypesApi.getBySlug(slug),
    enabled: !!slug,
  })
}

export function useCreateInterviewType() {
  const queryClient = useQueryClient()
  return useMutation<InterviewType, Error, CreateInterviewTypeDto>({
    mutationFn: (dto) => interviewTypesApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: interviewTypesKeys.lists() })
    },
  })
}

export function useUpdateInterviewType() {
  const queryClient = useQueryClient()
  return useMutation<InterviewType, Error, { id: string; dto: UpdateInterviewTypeDto }>({
    mutationFn: ({ id, dto }) => interviewTypesApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: interviewTypesKeys.lists() })
      queryClient.invalidateQueries({ queryKey: interviewTypesKeys.detail(data.id) })
    },
  })
}

export function useDeleteInterviewType() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => interviewTypesApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: interviewTypesKeys.lists() })
    },
  })
}
