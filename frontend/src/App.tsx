import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"
import { SearchDemo } from "@/components/demo/SearchDemo"
import { HeavyListDemo } from "@/components/demo/HeavyListDemo"

function App() {
  return (
    <div className="min-h-svh bg-background">
      {/* Hero */}
      <header className="flex flex-col items-center gap-6 px-4 pt-24 pb-16 text-center">
        <Badge className="rounded-full px-4 py-1 text-xs font-medium" variant="secondary">
          Tailwind v4 + Shadcn/ui
        </Badge>
        <h1 className="max-w-2xl text-4xl font-bold tracking-tight text-foreground sm:text-5xl">
          Frontend Architecture Ready
        </h1>
        <p className="max-w-md text-muted-foreground text-base sm:text-lg">
          Utility-first styling with Tailwind CSS v4 and accessible components from Shadcn/ui.
        </p>
        <div className="flex items-center gap-3">
          <Button>Get Started</Button>
          <Button variant="outline">Documentation</Button>
        </div>
      </header>

      {/* Component showcase */}
      <section className="mx-auto grid max-w-5xl grid-cols-1 gap-6 px-4 pb-24 sm:grid-cols-2 lg:grid-cols-3">
        <Card>
          <CardHeader>
            <CardTitle>Button Variants</CardTitle>
            <CardDescription>All built-in button styles</CardDescription>
          </CardHeader>
          <CardContent className="flex flex-wrap gap-2">
            <Button variant="default">Default</Button>
            <Button variant="secondary">Secondary</Button>
            <Button variant="outline">Outline</Button>
            <Button variant="ghost">Ghost</Button>
            <Button variant="destructive">Destructive</Button>
            <Button variant="link">Link</Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Form Elements</CardTitle>
            <CardDescription>Input with label</CardDescription>
          </CardHeader>
          <CardContent className="flex flex-col gap-4">
            <div className="flex flex-col gap-2">
              <Label htmlFor="name">Name</Label>
              <Input id="name" placeholder="Enter your name" />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="email">Email</Label>
              <Input id="email" type="email" placeholder="you@example.com" />
            </div>
          </CardContent>
          <CardFooter>
            <Button className="w-full">Submit</Button>
          </CardFooter>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Status Badges</CardTitle>
            <CardDescription>Different badge styles</CardDescription>
          </CardHeader>
          <CardContent className="flex flex-wrap gap-2">
            <Badge>Default</Badge>
            <Badge variant="secondary">Secondary</Badge>
            <Badge variant="outline">Outline</Badge>
            <Badge variant="destructive">Error</Badge>
            <Badge className="bg-emerald-100 text-emerald-700 dark:bg-emerald-900 dark:text-emerald-300">
              Success
            </Badge>
            <Badge className="bg-amber-100 text-amber-700 dark:bg-amber-900 dark:text-amber-300">
              Warning
            </Badge>
          </CardContent>
        </Card>

        {/* Dialog card spanning full width */}
        <Card className="sm:col-span-2 lg:col-span-3">
          <CardHeader>
            <CardTitle>Dialog Example</CardTitle>
            <CardDescription>Modal dialog with shadcn/ui</CardDescription>
          </CardHeader>
          <CardContent>
            <Dialog>
              <DialogTrigger asChild>
                <Button variant="outline">Open Dialog</Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Confirm Action</DialogTitle>
                  <DialogDescription>
                    This action will save your changes. Are you sure you want to proceed?
                  </DialogDescription>
                </DialogHeader>
                <div className="py-4 text-sm text-muted-foreground">
                  Your form data will be submitted for review.
                </div>
                <DialogFooter>
                  <Button variant="outline">Cancel</Button>
                  <Button>Confirm</Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          </CardContent>
        </Card>
      </section>

      {/* Hooks demo */}
      <section className="mx-auto grid max-w-5xl grid-cols-1 gap-6 px-4 pb-24 lg:grid-cols-2">
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
      </section>
    </div>
  )
}

export default App
