import { useState, useEffect, useMemo } from "react"
import { useDebouncedValue } from "@/hooks"

const DEMO_ITEMS = [
  "Fundamentos de React",
  "Patrones Avanzados de TypeScript",
  "Maestría en Tailwind CSS",
  "APIs REST con Spring Boot",
  "Optimización de PostgreSQL",
  "Despliegue con Docker y Kubernetes",
  "GraphQL con Apollo",
  "Next.js Full Stack",
  "Pruebas con Vitest",
  "CI/CD con GitHub Actions",
  "Microservicios con Node.js",
  "Ciencia de Datos con Python",
  "Programación de Sistemas con Rust",
  "Patrones de Concurrencia en Go",
  "Arquitectura en la Nube AWS",
]

export function SearchDemo() {
  const [query, setQuery] = useState("")
  const [searchCount, setSearchCount] = useState(0)
  const [keystrokeCount, setKeystrokeCount] = useState(0)
  const debouncedQuery = useDebouncedValue(query, 400)

  const results = useMemo(
    () =>
      DEMO_ITEMS.filter((item) =>
        item.toLowerCase().includes(debouncedQuery.toLowerCase()),
      ),
    [debouncedQuery],
  )

  useEffect(() => {
    if (debouncedQuery) {
      setSearchCount((c) => c + 1)
    }
  }, [debouncedQuery])

  return (
    <div className="flex flex-col gap-4">
      <div>
        <p className="mb-1 text-sm font-medium text-muted-foreground">
          {DEMO_ITEMS.length} cursos · La búsqueda se activa tras 400ms de pausa
        </p>
        <div className="relative">
          <input
            type="text"
            value={query}
            onChange={(e) => {
              setQuery(e.target.value)
              setKeystrokeCount((c) => c + 1)
            }}
            placeholder="Escribe para buscar cursos..."
            className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
          />
          {query && (
            <span className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-muted-foreground">
              {results.length} resultados
            </span>
          )}
        </div>
      </div>

      {/* Metrics */}
      <div className="flex gap-4 text-xs">
        <span className="rounded bg-muted px-2 py-1 text-muted-foreground">
          Teclas: {keystrokeCount}
        </span>
        <span className="rounded bg-muted px-2 py-1 text-muted-foreground">
          Búsquedas ejecutadas: {searchCount}
        </span>
        {keystrokeCount > 0 && (
          <span className="rounded bg-emerald-100 px-2 py-1 text-emerald-700 dark:bg-emerald-900 dark:text-emerald-300">
            Ahorraste {keystrokeCount - searchCount} llamadas API innecesarias
          </span>
        )}
      </div>

      {/* Results */}
      <ul className="flex flex-col gap-1">
        {results.length > 0 ? (
          results.map((item) => (
            <li
              key={item}
              className="rounded-md bg-muted/50 px-3 py-2 text-sm"
            >
              {item}
            </li>
          ))
        ) : (
          <li className="py-2 text-sm text-muted-foreground">
            {query ? "Ningún curso coincide con tu búsqueda." : "Empieza a escribir para filtrar cursos."}
          </li>
        )}
      </ul>
    </div>
  )
}
