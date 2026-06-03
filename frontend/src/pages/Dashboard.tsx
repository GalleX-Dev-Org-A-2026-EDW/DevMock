import MainLayout from "@/layouts/MainLayout"
import SidebarMenu from "@/components/SidebarMenu"

export default function Dashboard() {
  return (
    <MainLayout
      sidebar={<SidebarMenu />}
      content={
        <div className="flex items-center justify-center h-full">
          <div className="text-center">
            <h1 className="font-['Work_Sans'] font-bold text-3xl text-white mb-3">Dashboard</h1>
            <p className="text-white/60 text-lg">Próximamente</p>
          </div>
        </div>
      }
    />
  )
}
