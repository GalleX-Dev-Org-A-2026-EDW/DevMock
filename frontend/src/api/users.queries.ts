import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { usersApi } from "./users"
import type { User, CreateUserDto, UpdateUserDto } from "./users"
import { usersKeys } from "./users.keys"

export function useUsers() {
  return useQuery({
    queryKey: usersKeys.lists(),
    queryFn: () => usersApi.list(),
  })
}

export function useUser(id: string) {
  return useQuery({
    queryKey: usersKeys.detail(id),
    queryFn: () => usersApi.getById(id),
    enabled: !!id,
  })
}

export function useUserByEmail(email: string) {
  return useQuery({
    queryKey: usersKeys.byEmail(email),
    queryFn: () => usersApi.getByEmail(email),
    enabled: !!email,
  })
}

export function useMe() {
  return useQuery({
    queryKey: usersKeys.detail("me"),
    queryFn: () => usersApi.me(),
  })
}

export function useCreateUser() {
  const queryClient = useQueryClient()
  return useMutation<User, Error, CreateUserDto>({
    mutationFn: (dto) => usersApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: usersKeys.lists() })
    },
  })
}

export function useUpdateUser() {
  const queryClient = useQueryClient()
  return useMutation<User, Error, { id: string; dto: UpdateUserDto }>({
    mutationFn: ({ id, dto }) => usersApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: usersKeys.lists() })
      queryClient.invalidateQueries({ queryKey: usersKeys.detail(data.id) })
    },
  })
}

export function useDeleteUser() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => usersApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: usersKeys.lists() })
    },
  })
}
