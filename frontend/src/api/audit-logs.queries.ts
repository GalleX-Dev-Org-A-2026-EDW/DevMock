import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { auditLogsApi } from "./audit-logs"
import type { AuditLog, CreateAuditLogDto, UpdateAuditLogDto } from "./audit-logs"
import { auditLogsKeys } from "./audit-logs.keys"

export function useAuditLogs() {
  return useQuery({
    queryKey: auditLogsKeys.lists(),
    queryFn: () => auditLogsApi.list(),
  })
}

export function useAuditLog(id: string) {
  return useQuery({
    queryKey: auditLogsKeys.detail(id),
    queryFn: () => auditLogsApi.getById(id),
    enabled: !!id,
  })
}

export function useCreateAuditLog() {
  const queryClient = useQueryClient()
  return useMutation<AuditLog, Error, CreateAuditLogDto>({
    mutationFn: (dto) => auditLogsApi.create(dto),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: auditLogsKeys.lists() })
    },
  })
}

export function useUpdateAuditLog() {
  const queryClient = useQueryClient()
  return useMutation<AuditLog, Error, { id: string; dto: UpdateAuditLogDto }>({
    mutationFn: ({ id, dto }) => auditLogsApi.update(id, dto),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: auditLogsKeys.lists() })
      queryClient.invalidateQueries({ queryKey: auditLogsKeys.detail(data.id) })
    },
  })
}

export function useDeleteAuditLog() {
  const queryClient = useQueryClient()
  return useMutation<void, Error, string>({
    mutationFn: (id) => auditLogsApi.remove(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: auditLogsKeys.lists() })
    },
  })
}
