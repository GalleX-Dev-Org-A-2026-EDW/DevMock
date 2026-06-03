import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { sessionQuestionsApi } from "./session-questions"
import type { SessionQuestion, CreateSessionQuestionDto, UpdateSessionQuestionDto } from "./session-questions"
import { sessionQuestionsKeys } from "./session-questions.keys"

export function useSessionQuestions() {
  return useQuery({
    queryKey: sessionQuestionsKeys.lists(),
    queryFn: () => sessionQuestionsApi.list(),
  })
}

export function useSessionQuestion(id: string) {
  return useQuery({
    queryKey: sessionQuestionsKeys.detail(id),
    queryFn: () => sessionQuestionsApi.getById(id),
    enabled: !!id,
  })
}

export function useCreateSessionQuestion() {
  const queryClient = useQueryClient()
  return useMutation<SessionQuestion, Error, CreateSessionQuestionDto>({
    mutationFn: (dto) => sessionQuestionsApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: sessionQuestionsKeys.lists() })
    },
  })
}

export function useUpdateSessionQuestion() {
  const queryClient = useQueryClient()
  return useMutation<SessionQuestion, Error, { id: string; dto: UpdateSessionQuestionDto }>({
    mutationFn: ({ id, dto }) => sessionQuestionsApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: sessionQuestionsKeys.lists() })
      queryClient.invalidateQueries({ queryKey: sessionQuestionsKeys.detail(data.id) })
    },
  })
}

export function useDeleteSessionQuestion() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => sessionQuestionsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: sessionQuestionsKeys.lists() })
    },
  })
}
