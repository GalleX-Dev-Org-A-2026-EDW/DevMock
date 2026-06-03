import { useState, useEffect, useMemo } from "react"
import { useDebouncedValue } from "@/hooks"

const DEMO_ITEMS = [
  "React Fundamentals",
  "Advanced TypeScript Patterns",
  "Tailwind CSS Mastery",
  "Spring Boot REST APIs",
  "PostgreSQL Performance Tuning",
  "Docker & Kubernetes Deployment",
  "GraphQL with Apollo",
  "Next.js Full Stack",
  "Testing with Vitest",
  "CI/CD with GitHub Actions",
  "Node.js Microservices",
  "Python Data Science",
  "Rust Systems Programming",
  "Go Concurrency Patterns",
  "AWS Cloud Architecture",
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
          {DEMO_ITEMS.length} courses · Search triggers after 400ms pause
        </p>
        <div className="relative">
          <input
            type="text"
            value={query}
            onChange={(e) => {
              setQuery(e.target.value)
              setKeystrokeCount((c) => c + 1)
            }}
            placeholder="Type to search courses..."
            className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
          />
          {query && (
            <span className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-muted-foreground">
              {results.length} results
            </span>
          )}
        </div>
      </div>

      {/* Metrics */}
      <div className="flex gap-4 text-xs">
        <span className="rounded bg-muted px-2 py-1 text-muted-foreground">
          Keystrokes: {keystrokeCount}
        </span>
        <span className="rounded bg-muted px-2 py-1 text-muted-foreground">
          Searches fired: {searchCount}
        </span>
        {keystrokeCount > 0 && (
          <span className="rounded bg-emerald-100 px-2 py-1 text-emerald-700 dark:bg-emerald-900 dark:text-emerald-300">
            Saved {keystrokeCount - searchCount} unnecessary API calls
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
            {query ? "No courses match your search." : "Start typing to filter courses."}
          </li>
        )}
      </ul>
    </div>
  )
}
