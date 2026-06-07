# Frontend Architecture Guide — Tailwind CSS v4 + Shadcn/ui

> **Stack:** Vite 8 · React 19 · TypeScript 6 · Tailwind CSS v4 · Shadcn/ui  
> **Target audience:** Developers with basic React/TS knowledge, new to Tailwind/shadcn.

---

## Table of Contents

1. [Tailwind CSS Setup & Configuration](#1-tailwind-css-setup--configuration)
2. [Utility-First Concept Explained](#2-utility-first-concept-explained)
3. [Shadcn/ui Integration](#3-shadcnui-integration)
4. [Best Practices & Workflow](#4-best-practices--workflow)
5. [Quick Reference Examples](#5-quick-reference-examples)
6. [Common Mistakes & Gotchas](#6-common-mistakes--gotchas)

---

## 1. Tailwind CSS Setup & Configuration

### Prerequisites

This project uses **Tailwind CSS v4** — a major rewrite from v3. The key differences:

| v3 | v4 |
|---|---|
| `tailwind.config.js` for all config | CSS-first config via `@theme` directive |
| `@tailwind base/components/utilities` | Single `@import "tailwindcss"` |
| `content` array required | Auto-detection (no config needed) |
| Standalone PostCSS plugin | `@tailwindcss/postcss` plugin |

### What's Already Installed

Check `package.json` — the following devDependencies should exist:

```jsonc
// devDependencies (already present)
"@tailwindcss/postcss": "^4.3.0",
"autoprefixer": "^10.5.0",
"postcss": "^8.5.15",
"tailwindcss": "^4.3.0",
```

If missing, install:

```bash
npm install -D tailwindcss @tailwindcss/postcss postcss autoprefixer
```

### Step 1 — Create `postcss.config.js`

Tailwind v4 uses `@tailwindcss/postcss` as its PostCSS plugin:

```js
// postcss.config.js
export default {
  plugins: {
    '@tailwindcss/postcss': {},
    autoprefixer: {},
  },
}
```

> **Gotcha:** In Tailwind v3 you used `tailwindcss` as the plugin name. In v4 it's `@tailwindcss/postcss`. Using the old name will silently produce no output.

### Step 2 — Initialize Tailwind in CSS

Replace the contents of `src/index.css` with:

```css
@import "tailwindcss";
```

That's it. One line. Tailwind v4's `@import` replaces the three v3 directives (`@tailwind base`, `@tailwind components`, `@tailwind utilities`) and automatically includes Preflight (the CSS reset).

### Step 3 — Customizing the Design System (Optional)

In v3 you edited `tailwind.config.js`. In v4 you use the `@theme` directive in CSS:

```css
@import "tailwindcss";

@theme {
  --color-primary: #6366f1;
  --color-primary-dark: #4f46e5;
  --color-surface: #f8fafc;
  --font-family-display: "Inter", sans-serif;
  --radius-box: 0.75rem;
}
```

These tokens become available as Tailwind utilities: `bg-primary`, `text-primary-dark`, `rounded-box`, `font-display`.

> **Tip:** Run `npx tailwindcss --help` to see all v4 CLI options. Use `npx tailwindcss --input src/index.css --output dist/output.css` to preview the generated CSS.

### Step 4 — Verify It Works

Add a test class to any element in `App.tsx`:

```tsx
<div className="text-3xl font-bold text-indigo-600 underline">
  Tailwind v4 is working!
</div>
```

Then run:

```bash
npm run dev
```

If you see indigo, bold, underlined text, the setup is correct.

---

## 2. Utility-First Concept Explained

### The Core Idea

Instead of writing custom CSS classes, you compose styles directly in your HTML/JSX using **single-purpose utility classes**.

### Traditional CSS Approach

```css
/* styles.css */
.card {
  background-color: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}
.card-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
}
```

```tsx
<div className="card">
  <h2 className="card-title">Hello World</h2>
</div>
```

### Tailwind Utility Approach

```tsx
<div className="bg-white rounded-xl p-4 shadow-sm">
  <h2 className="text-xl font-semibold text-slate-900">Hello World</h2>
</div>
```

No custom CSS. Every class maps to a single CSS property:

| Class | CSS Output |
|---|---|
| `bg-white` | `background-color: #fff;` |
| `rounded-xl` | `border-radius: 0.75rem;` |
| `p-4` | `padding: 1rem;` |
| `shadow-sm` | `box-shadow: 0 1px 2px rgba(0,0,0,0.05);` |
| `text-xl` | `font-size: 1.25rem;` |
| `font-semibold` | `font-weight: 600;` |
| `text-slate-900` | `color: #0f172a;` |

### Responsive & State Variants

Tailwind uses **prefix variants** for responsive design and states:

```tsx
<div className="text-sm md:text-base lg:text-lg">Responsive text</div>
<button className="bg-blue-500 hover:bg-blue-600 focus:ring-2 disabled:opacity-50">
  Click me
</button>
```

| Prefix | Applies when |
|---|---|
| `sm:` | `min-width: 640px` |
| `md:` | `min-width: 768px` |
| `lg:` | `min-width: 1024px` |
| `hover:` | `:hover` state |
| `focus:` | `:focus` state |
| `dark:` | `prefers-color-scheme: dark` |
| `disabled:` | `:disabled` state |

### Why This Works

- **No naming fatigue** — no debating `.card-header` vs `.card-heading` vs `.card__title`
- **No specificity wars** — every class has the same specificity
- **No dead CSS** — unused classes are purged at build time
- **Cohesive design** — you're constrained to the design system (no random hex values)

---

## 3. Shadcn/ui Integration

### What Is Shadcn/ui?

**Shadcn/ui is not an npm package.** It's a collection of copy-paste React components built with Radix UI primitives and styled with Tailwind CSS. You run a CLI to add components to your project, then own and customize the source code directly.

### Why Shadcn/ui with Tailwind?

- **Tailwind-native** — components are styled with Tailwind classes, not a separate styling engine
- **Full control** — every component is editable in `src/components/ui/`
- **Accessible** — built on Radix UI (WAI-ARIA compliant)
- **Tree-shakeable** — only the components you add are in your codebase

### Installation

```bash
npx shadcn@latest init
```

The CLI will ask a few questions:

```
✔ Which style would you like to use? › New York (recommended)
✔ Which color would you like to use as the base? › Neutral
✔ Do you want to use CSS variables for theming? › Yes
✔ What is the path to your Tailwind config? › (leave blank — v4 auto-detects)
✔ Are you using React Server Components? › No
✔ Write components.json? › Yes
```

This will:

1. Create `components.json` with your preferences
2. Add CSS variables to `src/index.css`
3. Add the `@/` path alias to `vite.config.ts` and `tsconfig.app.json`
4. Create `src/lib/utils.ts` with the `cn()` helper

### After Init — `components.json`

```json
{
  "$schema": "https://ui.shadcn.com/schema.json",
  "style": "new-york",
  "rsc": false,
  "tsx": true,
  "tailwind": {
    "config": "",
    "css": "src/index.css",
    "baseColor": "neutral",
    "cssVariables": true
  },
  "aliases": {
    "components": "@/components",
    "utils": "@/lib/utils",
    "ui": "@/components/ui",
    "lib": "@/lib",
    "hooks": "@/hooks"
  }
}
```

### Adding Components

```bash
# Add individual components as needed
npx shadcn@latest add button
npx shadcn@latest add card
npx shadcn@latest add input
npx shadcn@latest add badge
npx shadcn@latest add dialog
```

Each command creates a file in `src/components/ui/` (e.g., `src/components/ui/button.tsx`).

### How Shadcn/ui Components Work with Tailwind

Shadcn/ui components accept a `className` prop that merges with their internal styles via the `cn()` utility:

```tsx
import { Button } from "@/components/ui/button"

// Internal styles + your overrides
<Button className="bg-red-500 hover:bg-red-600">
  Delete
</Button>
```

The `cn()` function uses `clsx` and `tailwind-merge` to intelligently combine classes without conflicts:

```ts
// src/lib/utils.ts
import { type ClassValue, clsx } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}
```

> If `clsx` and `tailwind-merge` aren't installed, run `npm install clsx tailwind-merge` — the shadcn init CLI should install them automatically.

---

## 4. Best Practices & Workflow

### Component Composition

**Layer your components from generic to specific:**

```
src/
├── components/
│   ├── ui/                  # shadcn/ui primitives (Button, Card, Input)
│   ├── shared/              # app-specific shared components (UserAvatar, PageHeader)
│   └── features/            # page/feature-specific components (LoginForm, DashboardChart)
├── lib/
│   └── utils.ts             # cn() helper
├── hooks/                   # custom hooks
├── pages/                   # route-level components
└── styles/
    └── index.css            # Tailwind imports + @theme
```

### When to Use Utilities vs. Component Library

| Use Case | Approach |
|---|---|
| One-off layout (margin, padding, flex) | Raw Tailwind classes |
| Semantic text styles (headings, body) | Raw Tailwind classes |
| Buttons, inputs, cards, dialogs | shadcn/ui (accessible, consistent) |
| Complex patterns (tables, comboboxes) | shadcn/ui (Radix handles behavior) |
| App-specific design (hero sections, banners) | Composed from utilities + shadcn |

### Keeping JSX Clean

Extract repetitive utility groups into reusable components:

```tsx
// ❌ Repetitive
<button className="inline-flex items-center justify-center rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-500">
  Save
</button>
<button className="inline-flex items-center justify-center rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-500">
  Cancel
</button>

// ✅ Abstracted
<Button variant="default">Save</Button>
<Button variant="outline">Cancel</Button>
```

### Dark Mode

Toggle dark mode via a class on `<html>`:

```tsx
// In your layout or provider
<html className="dark">
```

Tailwind's `dark:` variant handles the rest:

```tsx
<div className="bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100">
```

Shadcn/ui components handle dark mode automatically via CSS variables.

### Performance

- **Purging:** Tailwind v4 purges unused classes automatically (no config needed)
- **Bundle size:** A typical project uses ~10 KB of Tailwind CSS after purging
- **Shadcn/ui:** Components you don't add don't exist in your codebase

---

## 5. Quick Reference Examples

### Example 1 — Button Variants

```tsx
import { Button } from "@/components/ui/button"

export function ButtonDemo() {
  return (
    <div className="flex flex-wrap gap-4">
      <Button variant="default">Default</Button>
      <Button variant="secondary">Secondary</Button>
      <Button variant="outline">Outline</Button>
      <Button variant="ghost">Ghost</Button>
      <Button variant="destructive">Delete</Button>
      <Button variant="link">Link</Button>
      <Button disabled>Disabled</Button>
    </div>
  )
}
```

### Example 2 — Card with Tailwind Layout

```tsx
import { Button } from "@/components/ui/button"
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"

export function CourseCard() {
  return (
    <Card className="w-full max-w-sm">
      <CardHeader>
        <CardTitle>React Fundamentals</CardTitle>
        <CardDescription>
          Build your first component-based application
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex items-center gap-2 text-sm text-muted-foreground">
          <span className="rounded bg-emerald-100 px-2 py-0.5 text-xs font-medium text-emerald-700 dark:bg-emerald-900 dark:text-emerald-300">
            Beginner
          </span>
          <span>8 hours</span>
          <span>4 modules</span>
        </div>
        <p className="mt-4 text-sm text-muted-foreground">
          Learn components, state, props, effects, and hooks through
          hands-on exercises.
        </p>
      </CardContent>
      <CardFooter className="flex justify-between">
        <Button variant="outline">Details</Button>
        <Button>Enroll Free</Button>
      </CardFooter>
    </Card>
  )
}
```

### Example 3 — Form Input with Label

```tsx
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"

export function LoginForm() {
  return (
    <form className="mx-auto flex w-full max-w-sm flex-col gap-6">
      <div className="flex flex-col gap-2">
        <Label htmlFor="email">Email</Label>
        <Input
          id="email"
          type="email"
          placeholder="you@example.com"
          className="focus-visible:ring-2"
        />
      </div>
      <div className="flex flex-col gap-2">
        <Label htmlFor="password">Password</Label>
        <Input id="password" type="password" />
      </div>
      <Button type="submit" className="w-full">
        Sign In
      </Button>
    </form>
  )
}
```

### Example 4 — Badge + Dialog

```tsx
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"

export function StatusDialog() {
  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button variant="outline">View Status</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Submission Status</DialogTitle>
          <DialogDescription>
            Your latest code review results
          </DialogDescription>
        </DialogHeader>
        <div className="flex items-center gap-3 py-4">
          <span className="text-sm font-medium">Frontend challenge:</span>
          <Badge variant="secondary" className="bg-emerald-100 text-emerald-700 dark:bg-emerald-900 dark:text-emerald-300">
            Passed
          </Badge>
        </div>
        <p className="text-sm text-muted-foreground">
          Score: 92/100 — Correctness: 95%, Efficiency: 88%, Clarity: 93%
        </p>
        <DialogFooter>
          <Button type="button">Close</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
```

### Example 5 — Responsive Grid Layout

```tsx
import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

const skills = [
  { name: "React", level: "Advanced", color: "bg-sky-100 text-sky-700 dark:bg-sky-900 dark:text-sky-300" },
  { name: "TypeScript", level: "Intermediate", color: "bg-blue-100 text-blue-700 dark:bg-blue-900 dark:text-blue-300" },
  { name: "Tailwind", level: "Intermediate", color: "bg-teal-100 text-teal-700 dark:bg-teal-900 dark:text-teal-300" },
]

export function SkillsGrid() {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
      {skills.map((skill) => (
        <Card key={skill.name}>
          <CardHeader>
            <CardTitle className="text-lg">{skill.name}</CardTitle>
          </CardHeader>
          <CardContent>
            <Badge className={skill.color} variant="secondary">
              {skill.level}
            </Badge>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
```

---

## 6. Common Mistakes & Gotchas

| Mistake | Why It's Wrong | Fix |
|---|---|---|
| Using `tailwindcss` plugin in PostCSS (v3 pattern) | Tailwind v4 uses `@tailwindcss/postcss` | Use `'@tailwindcss/postcss': {}` |
| Creating `tailwind.config.js` | v4 doesn't read it — config is CSS-first | Use `@theme` in your CSS file |
| Import order: `@import "tailwindcss"` after custom styles | Tailwind's Preflight reset overrides your custom styles | Put `@import "tailwindcss"` first |
| Adding custom CSS in `index.css` outside `@theme` | Works, but defeats the purpose of using Tailwind | Use utilities or extract to components |
| Forgetting `darkMode: "class"` | v4 defaults to media-query-based dark mode | Add `.dark` class to `<html>` element |
| Importing shadcn/ui components from npm | They're not published on npm | Add via `npx shadcn@latest add` |
| Not running `npx shadcn@latest init` before adding components | The CLI needs `components.json` to configure paths | Run `init` first |
| Using `@/` imports without configuring path alias | TypeScript/Vite won't resolve them | Add alias in both `vite.config.ts` and `tsconfig.app.json` |
| Mixing Tailwind classes with `!important` | Creates specificity debt | Use `cn()` with `tailwind-merge` for conflict resolution |

---

## Quick Setup Checklist

- [ ] `postcss.config.js` created with `@tailwindcss/postcss`
- [ ] `src/index.css` has `@import "tailwindcss"`
- [ ] `npm run dev` — Tailwind classes render correctly
- [ ] `npx shadcn@latest init` completed
- [ ] `@/` path alias configured in Vite + TypeScript
- [ ] At least one shadcn/ui component added and rendering
- [ ] `App.css` removed (no conflicting custom styles)
- [ ] `npm run build` succeeds
