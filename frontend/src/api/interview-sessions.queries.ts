import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { interviewSessionsApi } from "./interview-sessions"
import type { InterviewSession, CreateInterviewSessionDto, UpdateInterviewSessionDto } from "./interview-sessions"
import { interviewSessionsKeys } from "./interview-sessions.keys"

export function useInterviewSessions() {
  return useQuery({
    queryKey: interviewSessionsKeys.lists(),
    queryFn: () => interviewSessionsApi.list(),
  })
}

export function useInterviewSession(id: string) {
  return useQuery({
    queryKey: interviewSessionsKeys.detail(id),
    queryFn: () => interviewSessionsApi.getById(id),
    enabled: !!id,
  })
}

export function useCreateInterviewSession() {
  const queryClient = useQueryClient()
  return useMutation<InterviewSession, Error, CreateInterviewSessionDto>({
    mutationFn: (dto) => interviewSessionsApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: interviewSessionsKeys.lists() })
    },
  })
}

export function useUpdateInterviewSession() {
  const queryClient = useQueryClient()
  return useMutation<InterviewSession, Error, { id: string; dto: UpdateInterviewSessionDto }>({
    mutationFn: ({ id, dto }) => interviewSessionsApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: interviewSessionsKeys.lists() })
      queryClient.invalidateQueries({ queryKey: interviewSessionsKeys.detail(data.id) })
    },
  })
}

export function useDeleteInterviewSession() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => interviewSessionsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: interviewSessionsKeys.lists() })
    },
  })
}
