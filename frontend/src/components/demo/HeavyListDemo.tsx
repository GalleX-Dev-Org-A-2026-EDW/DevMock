import { useState, useMemo, type ChangeEvent } from "react"
import { useDeferredValue } from "@/hooks"

interface LogEntry {
  id: number
  label: string
  category: string
  priority: "baja" | "media" | "alta"
  timestamp: string
}

function generateLogs(count: number): LogEntry[] {
  const categories = ["sistema", "red", "seguridad", "base-datos", "cache"]
  const priorities: LogEntry["priority"][] = ["baja", "media", "alta"]

  return Array.from({ length: count }, (_, i) => ({
    id: i,
    label: `Evento #${i} — ${Math.random().toString(36).slice(2, 8)}`,
    category: categories[i % categories.length],
    priority: priorities[i % priorities.length],
    timestamp: new Date(Date.now() - i * 60000).toISOString(),
  }))
}

const ALL_LOGS = generateLogs(5000)

function ExpensiveLogTable({ logs }: { logs: LogEntry[] }) {
  // Simulate heavy computation: sort + format timestamps + build rows
  const processed = useMemo(() => {
    let result = 0
    for (let i = 0; i < 500_000; i++) {
      result += (i * 0.5) | 0
    }

    return logs
      .slice()
      .sort((a, b) => b.id - a.id)
      .map((log) => ({
        ...log,
        formattedTime: new Date(log.timestamp).toLocaleString(),
      }))
  }, [logs])

  return (
    <div className="max-h-80 overflow-y-auto rounded-md border">
      <table className="w-full text-left text-xs">
        <thead className="sticky top-0 bg-muted">
          <tr>
            <th className="px-3 py-2 font-medium">Evento</th>
            <th className="px-3 py-2 font-medium">Categoría</th>
            <th className="px-3 py-2 font-medium">Prioridad</th>
            <th className="px-3 py-2 font-medium">Hora</th>
          </tr>
        </thead>
        <tbody>
          {processed.map((log) => (
            <tr
              key={log.id}
              className="border-t border-border transition-colors hover:bg-muted/50"
            >
              <td className="px-3 py-1.5 font-mono">{log.label}</td>
              <td className="px-3 py-1.5 text-muted-foreground">
                {log.category}
              </td>
              <td className="px-3 py-1.5">
                <span
                  className={
                    log.priority === "alta"
                      ? "text-red-500"
                      : log.priority === "media"
                        ? "text-amber-500"
                        : "text-muted-foreground"
                  }
                >
                  {log.priority}
                </span>
              </td>
              <td className="px-3 py-1.5 text-muted-foreground">
                {log.formattedTime}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export function HeavyListDemo() {
  const [filter, setFilter] = useState("")
  const { deferredValue: deferredFilter, isPending } =
    useDeferredValue(filter, 2000)

  const filteredLogs = useMemo(
    () =>
      ALL_LOGS.filter(
        (log) =>
          log.label.toLowerCase().includes(deferredFilter.toLowerCase()) ||
          log.category.includes(deferredFilter.toLowerCase()),
      ),
    [deferredFilter],
  )

  function handleChange(e: ChangeEvent<HTMLInputElement>) {
    setFilter(e.target.value)
  }

  return (
    <div className="flex flex-col gap-4">
      <div>
        <p className="mb-1 text-sm font-medium text-muted-foreground">
          5,000 entradas de log · El input se actualiza al instante, la tabla se renderiza diferida
        </p>
        <div className="relative">
          <input
            type="text"
            value={filter}
            onChange={handleChange}
            placeholder="Filtrar logs..."
            className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
          />
          {isPending && (
            <span className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-amber-500">
              renderizando…
            </span>
          )}
        </div>
      </div>

      <div className="flex items-center gap-2 text-xs text-muted-foreground">
        <span>Mostrando {filteredLogs.length} de {ALL_LOGS.length} entradas</span>
        {isPending && (
          <span className="rounded bg-amber-100 px-2 py-0.5 text-amber-700 dark:bg-amber-900 dark:text-amber-300">
            Renderizado diferido en progreso
          </span>
        )}
      </div>

      <ExpensiveLogTable logs={filteredLogs} />
    </div>
  )
}
