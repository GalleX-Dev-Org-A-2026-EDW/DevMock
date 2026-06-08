import { useState, useEffect, useRef } from "react"

/**
 * useDeferredValue
 *
 * Returns a deferred (low-priority) version of the input value. Updates to
 * the deferred value are intentionally delayed so the browser stays responsive
 * during expensive renders.
 *
 * How it differs from `useDebouncedValue`:
 * - `useDebouncedValue` waits for a pause, then updates once. Best for
 *   reducing *function calls* (API requests, validation).
 * - `useDeferredValue` splits the update into two phases: urgent first
 *   (the raw value), then deferred (the heavy render). Best for reducing
 *   *visual jank* from expensive rendering.
 *
 * Use case:
 * - A search input that filters a large list: the keystroke feels instant
 *   because the input updates immediately, while the filtered list renders
 *   in the background without blocking.
 *
 * Performance considerations:
 * - The deferred update uses `requestAnimationFrame` to yield to the
 *   browser's paint cycle before doing heavy work.
 * - If updates arrive faster than the frame rate, intermediate deferred
 *   values are dropped — React never renders stale intermediate states.
 * - Combine with `useMemo` on the expensive computation to skip work when
 *   the deferred value hasn't actually changed.
 *
 * @param value      The value to defer.
 * @param timeoutMs  Maximum time (ms) before the deferred value forces an
 *                   update even if the browser is busy (default 0 = no
 *                   timeout).
 * @returns          The deferred value, plus a flag indicating whether the
 *                   deferred render is still pending.
 *
 * @example
 * ```tsx
 * const { deferredValue, isPending } = useDeferredValue(searchTerm)
 *
 * // Fast: updates every keystroke
 * <input value={searchTerm} onChange={…} />
 *
 * // Deferred: only updates when the browser is free
 * <ExpensiveList items={filterItems(data, deferredValue)} />
 * ```
 */
export function useDeferredValue<T>(value: T, timeoutMs: number = 0) {
  const [deferred, setDeferred] = useState(value)
  const [isPending, setIsPending] = useState(false)
  const rafRef = useRef<number | null>(null)
  const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null)
  const previousValue = useRef(value)
  const startTimeRef = useRef<number | null>(null)

  useEffect(() => {
    // Nothing to defer if the value hasn't changed
    if (Object.is(previousValue.current, value)) {
      return
    }
    previousValue.current = value

    setIsPending(true)

    // Cancel any scheduled updates
    if (rafRef.current !== null) {
      cancelAnimationFrame(rafRef.current)
    }
    if (timeoutRef.current !== null) {
      clearTimeout(timeoutRef.current)
    }

    // If a timeout is configured, force the update after `timeoutMs`
    if (timeoutMs > 0) {
      if (startTimeRef.current === null) {
        startTimeRef.current = performance.now()
      }
      timeoutRef.current = setTimeout(() => {
        if (rafRef.current !== null) {
          cancelAnimationFrame(rafRef.current)
          rafRef.current = null
        }
        setDeferred(value)
        setIsPending(false)
        startTimeRef.current = null
      }, timeoutMs)
    }

    // Schedule the deferred update at the next paint cycle
    rafRef.current = requestAnimationFrame(() => {
      setDeferred(value)
      setIsPending(false)
      startTimeRef.current = null
    })

    return () => {
      if (rafRef.current !== null) {
        cancelAnimationFrame(rafRef.current)
      }
      if (timeoutRef.current !== null) {
        clearTimeout(timeoutRef.current)
      }
    }
  }, [value, timeoutMs])

  return { deferredValue: deferred, isPending }
}
