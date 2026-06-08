import { useState } from "react"
import { useAuditLogs } from "@/api/audit-logs.queries"
import { ClipboardList } from "lucide-react"

function getPageRange(current: number, total: number): (number | "...")[] {
  if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1)
  const pages: (number | "...")[] = [1]
  if (current > 3) pages.push("...")
  const start = Math.max(2, current - 1)
  const end = Math.min(total - 1, current + 1)
  for (let i = start; i <= end; i++) pages.push(i)
  if (current < total - 2) pages.push("...")
  if (total > 1) pages.push(total)
  return pages
}

const actionLabels: Record<string, string> = {
  CREATE: "Creación",
  UPDATE: "Actualización",
  DELETE: "Eliminación",
  LOGIN: "Inicio sesión",
  LOGOUT: "Cierre sesión",
}

const actionColors: Record<string, string> = {
  CREATE: "bg-emerald-500/10 text-emerald-300 ring-1 ring-inset ring-emerald-500/20",
  UPDATE: "bg-blue-500/10 text-blue-300 ring-1 ring-inset ring-blue-500/20",
  DELETE: "bg-red-500/10 text-red-300 ring-1 ring-inset ring-red-500/20",
  LOGIN: "bg-violet-500/10 text-violet-300 ring-1 ring-inset ring-violet-500/20",
  LOGOUT: "bg-white/[0.08] text-white/50",
}

export default function AdminAuditLogs() {
  const PAGE_SIZE = 10
  const { data: items, isLoading, isError } = useAuditLogs()
  const [page, setPage] = useState(1)

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[40vh]">
        <div className="h-8 w-8 border-2 border-emerald-400 border-t-transparent rounded-full animate-spin" />
        <p className="text-white/50 text-sm mt-3">Cargando registros...</p>
      </div>
    )
  }

  if (isError) {
    return (
      <div className="p-4 text-center">
        <p className="text-red-400">Error al cargar registros de auditoría</p>
      </div>
    )
  }

  const totalPages = items ? Math.max(1, Math.ceil(items.length / PAGE_SIZE)) : 1
  const currentPage = Math.min(page, totalPages)
  const paginatedItems = items?.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE)

  return (
    <div>
      <div className="flex items-center gap-3 mb-6">
        <ClipboardList className="h-5 w-5 sm:h-6 sm:w-6 text-white flex-shrink-0" />
        <h1 className="font-['Work_Sans'] font-bold text-xl sm:text-2xl text-white">Registros de Auditoría</h1>
      </div>

      <div className="bg-white/[0.04] backdrop-blur-sm rounded-xl border border-white/[0.06] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-white/[0.06] bg-white/[0.03]">
                <th className="text-left px-4 py-3 font-semibold text-white/50">Acción</th>
                <th className="text-left px-4 py-3 font-semibold text-white/50">Entidad</th>
                <th className="text-left px-4 py-3 font-semibold text-white/50">ID Entidad</th>
                <th className="text-left px-4 py-3 font-semibold text-white/50">Usuario</th>
                <th className="text-left px-4 py-3 font-semibold text-white/50">IP</th>
                <th className="text-left px-4 py-3 font-semibold text-white/50">Fecha</th>
              </tr>
            </thead>
            <tbody>
              {paginatedItems?.map((log) => (
                <tr key={log.id} className="border-b border-white/[0.04] hover:bg-white/[0.03] transition-colors">
                  <td className="px-4 py-3">
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${actionColors[log.action] || "bg-white/[0.08] text-white/50"}`}>
                      {actionLabels[log.action] || log.action}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-white/90 font-medium">{log.entityName || "-"}</td>
                  <td className="px-4 py-3 text-white/50 font-['JetBrains_Mono'] text-xs">{log.entityId ? log.entityId.substring(0, 8) + "..." : "-"}</td>
                  <td className="px-4 py-3 text-white/50">{log.userId ? log.userId.substring(0, 8) + "..." : "-"}</td>
                  <td className="px-4 py-3 font-['JetBrains_Mono'] text-xs text-white/50">{log.ipAddress || "-"}</td>
                  <td className="px-4 py-3 text-white/50 text-xs">{log.createdAt ? new Date(log.createdAt).toLocaleString("es-CO") : "-"}</td>
                </tr>
              ))}
              {(!items || items.length === 0) && (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-white/40">No hay registros de auditoría</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {items && items.length > PAGE_SIZE && (
        <div className="flex items-center justify-between mt-4 px-1">
          <p className="text-sm text-white/40">
            {items.length} registros — Página {currentPage} de {totalPages}
          </p>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setPage((p) => Math.max(1, p - 1))}
              disabled={currentPage <= 1}
              className="px-3 py-1.5 rounded-lg text-sm font-medium bg-white/[0.04] text-white/60 hover:bg-white/[0.08] disabled:opacity-30 disabled:cursor-not-allowed transition-all border border-white/[0.06]"
            >
              Anterior
            </button>
            {getPageRange(currentPage, totalPages).map((p, i) =>
              p === "..." ? (
                <span key={`e${i}`} className="h-8 w-8 flex items-center justify-center text-white/30 text-xs select-none">
                  ...
                </span>
              ) : (
                <button
                  key={p}
                  onClick={() => setPage(p)}
                  className={`h-8 w-8 rounded-lg text-sm font-medium transition-all ${
                    p === currentPage
                      ? "bg-white/[0.1] text-white border border-white/[0.1]"
                      : "text-white/40 hover:text-white/70 hover:bg-white/[0.04] border border-transparent"
                  }`}
                >
                  {p}
                </button>
              )
            )}
            <button
              onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
              disabled={currentPage >= totalPages}
              className="px-3 py-1.5 rounded-lg text-sm font-medium bg-white/[0.04] text-white/60 hover:bg-white/[0.08] disabled:opacity-30 disabled:cursor-not-allowed transition-all border border-white/[0.06]"
            >
              Siguiente
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
