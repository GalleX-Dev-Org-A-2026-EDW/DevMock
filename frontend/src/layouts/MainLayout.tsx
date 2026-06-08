import { useState } from "react"
import { Menu, X } from "lucide-react"
import type { ReactNode } from "react"

interface MainLayoutProps {
  sidebar: ReactNode
  content: ReactNode
}

export default function MainLayout({ sidebar, content }: MainLayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(false)

  return (
    <div className="flex min-h-screen bg-gradient-to-br from-[#1a1a2e] via-[#16213e] to-[#0f3460]">
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-40 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      <aside className={`
        w-64 bg-gradient-to-br from-[#1a1a2e] via-[#16213e] to-[#0f3460] p-4
        fixed inset-y-0 left-0 z-50 transform transition-transform duration-200
        lg:relative lg:translate-x-0
        ${sidebarOpen ? "translate-x-0" : "-translate-x-full"}
      `}>
        <div className="flex justify-end lg:hidden mb-4">
          <button onClick={() => setSidebarOpen(false)} className="p-1 text-white/70 hover:text-white transition-colors">
            <X className="h-5 w-5" />
          </button>
        </div>
        {sidebar}
      </aside>

      <main className="flex-1 p-4 sm:p-6 min-w-0">
        <button
          onClick={() => setSidebarOpen(true)}
          className="lg:hidden mb-4 p-2 text-white/70 hover:text-white transition-colors"
        >
          <Menu className="h-5 w-5" />
        </button>
        {content}
      </main>
    </div>
  )
}
