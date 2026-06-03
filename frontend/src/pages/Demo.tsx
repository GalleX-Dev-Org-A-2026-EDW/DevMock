import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { SearchDemo } from "@/components/demo/SearchDemo"
import { HeavyListDemo } from "@/components/demo/HeavyListDemo"

export default function Demo() {
  return (
    <div className="mx-auto grid max-w-5xl grid-cols-1 gap-6 px-4 py-12 lg:grid-cols-2">
      <Card className="lg:col-span-2">
        <CardHeader>
          <CardTitle>useDebouncedValue</CardTitle>
          <CardDescription>
            Reduces function calls by waiting for a pause before committing
            the value. The debounced value stays stable while the user is
            still typing — the search only fires after a 400ms pause.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <SearchDemo />
        </CardContent>
      </Card>

      <Card className="lg:col-span-2">
        <CardHeader>
          <CardTitle>useDeferredValue</CardTitle>
          <CardDescription>
            Keeps the input responsive during expensive renders. The input
            value updates immediately on every keystroke, but the heavy
            filtered table only renders when the browser has spare cycles.
            Watch for the "rendering…" indicator when typing fast.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <HeavyListDemo />
        </CardContent>
      </Card>
    </div>
  )
}
