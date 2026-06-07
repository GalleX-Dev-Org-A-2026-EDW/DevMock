import { http, httpRequired } from "./http"
import type { AuditAction } from "./enums"

export interface AuditLog {
  id: string
  action: AuditAction
  entityName: string | null
  entityId: string | null
  oldValues: string | null
  newValues: string | null
  ipAddress: string | null
  userAgent: string | null
  userId: string | null
  createdAt: string
}

export interface CreateAuditLogDto {
  action: AuditAction
  entityName?: string
  entityId?: string
  oldValues?: string
  newValues?: string
  ipAddress?: string
  userAgent?: string
  userId?: string
}

export interface UpdateAuditLogDto {
  action?: AuditAction
  entityName?: string
  entityId?: string
  oldValues?: string
  newValues?: string
  ipAddress?: string
  userAgent?: string
  userId?: string
}

export const auditLogsApi = {
  list: () => http<AuditLog[]>("/api/audit-logs"),

  getById: (id: string) => http<AuditLog>(`/api/audit-logs/${id}`),

  create: (dto: CreateAuditLogDto) =>
    httpRequired<AuditLog>("/api/audit-logs", {
      method: "POST",
      body: JSON.stringify(dto),
    }),

  update: (id: string, dto: UpdateAuditLogDto) =>
    httpRequired<AuditLog>(`/api/audit-logs/${id}`, {
      method: "PUT",
      body: JSON.stringify(dto),
    }),

  remove: (id: string) =>
    httpRequired<void>(`/api/audit-logs/${id}`, { method: "DELETE" }),
}
