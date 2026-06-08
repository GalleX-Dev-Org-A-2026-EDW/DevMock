import { useEffect, useState } from "react"

/**
 * useDebouncedValue
 *
 * Returns a debounced version of the input value. The debounced value only
 * updates after the user pauses changing `value` for `delay` milliseconds.
 *
 * How it works:
 * 1. Every time `value` changes, `useEffect` fires and sets a `setTimeout`.
 * 2. If `value` changes again before the timer fires, `clearTimeout` cancels
 *    the previous timer (cleanup function).
 * 3. Only when `value` stays stable for `delay` ms does the debounced value
 *    actually update, triggering a re-render.
 *
 * Why `clearTimeout` is critical:
 * Without cleanup, every keystroke would queue an update. The debounced value
 * would still update N times—just N ms delayed. The cleanup ensures only the
 * *last* timer survives, so only one update occurs after the user stops typing.
 *
 * @param value  The raw, frequently-changing value.
 * @param delay  Milliseconds to wait before committing (default 300).
 * @returns      The stable, debounced value.
 *
 * @example
 * ```tsx
 * const [input, setInput] = useState("")
 * const debounced = useDebouncedValue(input, 400)
 *
 * useEffect(() => {
 *   fetchSearchResults(debounced) // only called after user pauses
 * }, [debounced])
 * ```
 */
export function useDebouncedValue<T>(value: T, delay: number = 300): T {
  const [debounced, setDebounced] = useState(value)

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebounced(value)
    }, delay)

    return () => {
      clearTimeout(timer)
    }
  }, [value, delay])

  return debounced
}
