import { useMutation } from "@tanstack/react-query"
import { authApi } from "./auth"
import type { LoginRequest, RegisterRequest, AuthResponse } from "./auth"

export function useLogin() {
  return useMutation<AuthResponse, Error, LoginRequest>({
    mutationFn: (dto) => authApi.login(dto),
  })
}

export function useRegister() {
  return useMutation<AuthResponse, Error, RegisterRequest>({
    mutationFn: (dto) => authApi.register(dto),
  })
}
