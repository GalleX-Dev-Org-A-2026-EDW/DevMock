import { Outlet, Link, useLocation } from "react-router-dom"

const navItems = [
  { to: "/dashboard", label: "Dashboard" },
  { to: "/demo", label: "Hooks Demo" },
]

export default function MainLayout() {
  const { pathname } = useLocation()

  return (
    <div className="flex min-h-screen bg-gray-100">
      <aside className="flex w-64 flex-col bg-white border-r">
        <div className="p-4 border-b">
          <h1 className="text-lg font-bold">DevMock</h1>
        </div>
        <nav className="flex flex-col gap-1 p-2">
          {navItems.map(({ to, label }) => (
            <Link
              key={to}
              to={to}
              className={`rounded-md px-3 py-2 text-sm font-medium transition-colors ${
                pathname.startsWith(to)
                  ? "bg-neutral-100 text-neutral-900"
                  : "text-neutral-600 hover:bg-neutral-50 hover:text-neutral-900"
              }`}
            >
              {label}
            </Link>
          ))}
        </nav>
      </aside>
      <main className="flex-1">
        <Outlet />
      </main>
    </div>
  )
}
