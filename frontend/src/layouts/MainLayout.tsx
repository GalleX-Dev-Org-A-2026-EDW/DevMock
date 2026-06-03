import type { ReactNode } from "react"

interface MainLayoutProps {
  sidebar: ReactNode
  content: ReactNode
}

export default function MainLayout({ sidebar, content }: MainLayoutProps) {
  return (
    <div className="flex min-h-screen bg-gradient-to-br from-[#1a1a2e] via-[#16213e] to-[#0f3460]">
      <aside className="w-64 bg-gradient-to-br from-[#1a1a2e] via-[#16213e] to-[#0f3460] p-4">
        {sidebar}
      </aside>
      <main className="flex-1 p-6">
        {content}
      </main>
    </div>
  )
}
