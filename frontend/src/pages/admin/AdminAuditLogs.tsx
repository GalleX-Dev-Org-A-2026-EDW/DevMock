import { useAuditLogs } from "@/api/audit-logs.queries"
import { ClipboardList } from "lucide-react"

const actionLabels: Record<string, string> = {
  CREATE: "Creación",
  UPDATE: "Actualización",
  DELETE: "Eliminación",
  LOGIN: "Inicio sesión",
  LOGOUT: "Cierre sesión",
}

const actionColors: Record<string, string> = {
  CREATE: "bg-emerald-50 text-emerald-700",
  UPDATE: "bg-blue-50 text-blue-700",
  DELETE: "bg-red-50 text-red-700",
  LOGIN: "bg-violet-50 text-violet-700",
  LOGOUT: "bg-gray-50 text-gray-600",
}

export default function AdminAuditLogs() {
  const { data: items, isLoading, isError } = useAuditLogs()

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="h-8 w-8 border-2 border-emerald-500 border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  if (isError) {
    return <p className="text-red-400">Error al cargar registros de auditoría</p>
  }

  return (
    <div>
      <div className="flex items-center gap-3 mb-6">
        <ClipboardList className="h-5 w-5 sm:h-6 sm:w-6 text-white flex-shrink-0" />
        <h1 className="font-['Work_Sans'] font-bold text-xl sm:text-2xl text-white">Registros de Auditoría</h1>
      </div>

      <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-100 bg-gray-50/50">
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Acción</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Entidad</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">ID Entidad</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Usuario</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">IP</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Fecha</th>
              </tr>
            </thead>
            <tbody>
              {items?.map((log) => (
                <tr key={log.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                  <td className="px-4 py-3">
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${actionColors[log.action] || "bg-gray-50 text-gray-600"}`}>
                      {actionLabels[log.action] || log.action}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-gray-900 font-medium">{log.entityName || "-"}</td>
                  <td className="px-4 py-3 text-gray-500 font-['JetBrains_Mono'] text-xs">{log.entityId ? log.entityId.substring(0, 8) + "..." : "-"}</td>
                  <td className="px-4 py-3 text-gray-500">{log.userId ? log.userId.substring(0, 8) + "..." : "-"}</td>
                  <td className="px-4 py-3 font-['JetBrains_Mono'] text-xs text-gray-500">{log.ipAddress || "-"}</td>
                  <td className="px-4 py-3 text-gray-500 text-xs">{log.createdAt ? new Date(log.createdAt).toLocaleString("es-CO") : "-"}</td>
                </tr>
              ))}
              {(!items || items.length === 0) && (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-gray-400">No hay registros de auditoría</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
