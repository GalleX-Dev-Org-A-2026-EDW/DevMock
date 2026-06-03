import { useState } from "react"
import { useAuth } from "@/context/AuthContext"
import MainLayout from "@/layouts/MainLayout"
import SidebarMenu from "@/components/SidebarMenu"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { useQuestions } from "@/api/questions.queries"
import { useInterviewSessions } from "@/api/interview-sessions.queries"
import { useUserPerformances } from "@/api/user-performances.queries"
import { useUserAchievements } from "@/api/user-achievements.queries"
import { useRankings } from "@/api/rankings.queries"

function StatCard({ title, value, loading }: { title: string; value: string | number; loading?: boolean }) {
  return (
    <Card>
      <CardHeader className="pb-2">
        <CardTitle className="text-sm text-muted-foreground font-medium">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <p className="text-3xl font-bold">{loading ? "—" : value}</p>
      </CardContent>
    </Card>
  )
}

function HomeView() {
  const { user } = useAuth()
  const { data: questions, isLoading: qLoading } = useQuestions()
  const { data: sessions, isLoading: sLoading } = useInterviewSessions()
  const { data: performances, isLoading: pLoading } = useUserPerformances()
  const { data: userAchievements, isLoading: aLoading } = useUserAchievements()
  const { data: rankings, isLoading: rLoading } = useRankings()

  const completedSessions = sessions?.filter(s => s.status === "COMPLETED") ?? []
  const totalScore = completedSessions.reduce((sum, s) => sum + (s.finalScore ?? 0), 0)
  const avgScore = completedSessions.length > 0
    ? (totalScore / completedSessions.length).toFixed(1)
    : "—"

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-bold">Bienvenido, {user?.username ?? "Usuario"}</h1>
        <p className="text-muted-foreground mt-1">Panel de control de tu preparación</p>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Preguntas disponibles" value={questions?.length ?? 0} loading={qLoading} />
        <StatCard title="Sesiones completadas" value={completedSessions.length} loading={sLoading} />
        <StatCard title="Puntaje promedio" value={avgScore} loading={pLoading} />
        <StatCard title="Logros obtenidos" value={userAchievements?.length ?? 0} loading={aLoading} />
        <StatCard title="Usuarios en ranking" value={rankings?.length ?? 0} loading={rLoading} />
        <StatCard
          title="Total preguntas respondidas"
          value={performances?.reduce((sum, p) => sum + p.totalQuestionsAnswered, 0) ?? 0}
          loading={pLoading}
        />
      </div>

      <section>
        <h2 className="text-lg font-semibold mb-4">Acciones rápidas</h2>
        <div className="flex flex-wrap gap-3">
          <Button>Nueva entrevista</Button>
          <Button variant="outline">Ver preguntas</Button>
          <Button variant="outline">Ver ranking</Button>
        </div>
      </section>
    </div>
  )
}

function QuestionsView() {
  const { data: questions, isLoading } = useQuestions()

  if (isLoading) return <p className="text-muted-foreground">Cargando preguntas…</p>

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold">Preguntas</h2>
      {!questions || questions.length === 0 ? (
        <p className="text-muted-foreground">No hay preguntas disponibles.</p>
      ) : (
        <div className="grid gap-3">
          {questions.map((q) => (
            <Card key={q.id}>
              <CardContent className="pt-6">
                <p className="font-medium">{q.statement}</p>
                <p className="text-sm text-muted-foreground mt-1">
                  {q.questionType} · {q.basePoints} pts
                </p>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}

function SessionsView() {
  const { data: sessions, isLoading } = useInterviewSessions()

  if (isLoading) return <p className="text-muted-foreground">Cargando sesiones…</p>

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold">Sesiones</h2>
      {!sessions || sessions.length === 0 ? (
        <p className="text-muted-foreground">No hay sesiones registradas.</p>
      ) : (
        <div className="grid gap-3">
          {sessions.map((s) => (
            <Card key={s.id}>
              <CardContent className="pt-6 flex items-center justify-between">
                <div>
                  <p className="font-medium capitalize">{s.status.toLowerCase().replace("_", " ")}</p>
                  <p className="text-sm text-muted-foreground">
                    {s.finalScore != null ? `Puntaje: ${s.finalScore}` : "Sin puntaje"}
                  </p>
                </div>
                <p className="text-xs text-muted-foreground">{new Date(s.createdAt).toLocaleDateString("es")}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}

function RankingView() {
  const { data: rankings, isLoading } = useRankings()

  if (isLoading) return <p className="text-muted-foreground">Cargando ranking…</p>

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold">Ranking</h2>
      {!rankings || rankings.length === 0 ? (
        <p className="text-muted-foreground">No hay datos de ranking disponibles.</p>
      ) : (
        <div className="grid gap-2">
          {rankings.map((r, i) => (
            <Card key={r.id}>
              <CardContent className="pt-6 flex items-center gap-4">
                <span className="text-lg font-bold text-muted-foreground w-8">#{i + 1}</span>
                <div>
                  <p className="font-medium">{r.userId ?? "Usuario"}</p>
                  <p className="text-sm text-muted-foreground">Puntaje: {r.totalScore ?? "—"}</p>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}

export default function Dashboard() {
  const [current, setCurrent] = useState("default")

  const renderContent = () => {
    switch (current) {
      case "questions":
        return <QuestionsView />
      case "sessions":
        return <SessionsView />
      case "ranking":
        return <RankingView />
      default:
        return <HomeView />
    }
  }

  return (
    <MainLayout
      sidebar={<SidebarMenu current={current} onChange={setCurrent} />}
      content={renderContent()}
    />
  )
}
