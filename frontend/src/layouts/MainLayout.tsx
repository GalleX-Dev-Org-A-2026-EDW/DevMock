import type { ReactNode } from "react"

interface MainLayoutProps {
  sidebar: ReactNode
  content: ReactNode
}

export default function MainLayout({ sidebar, content }: MainLayoutProps) {
  return (
    <div className="flex min-h-screen bg-gray-100">
      <aside className="w-64 bg-white border-r p-4">
        {sidebar}
      </aside>
      <main className="flex-1 p-6">
        {content}
      </main>
    </div>
  )
}
