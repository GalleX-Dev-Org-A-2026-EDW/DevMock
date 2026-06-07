import { useState } from "react"
import { useCategories } from "@/api/categories.queries"
import { useDifficultyLevels } from "@/api/difficulty-levels.queries"
import { useInterviewTypes } from "@/api/interview-types.queries"
import { useQuestions } from "@/api/questions.queries"
import { useCreateInterviewSession } from "@/api/interview-sessions.queries"
import { useCreateSessionQuestion } from "@/api/session-questions.queries"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import type { InterviewType } from "@/api/interview-types"
import type { Category } from "@/api/categories"
import type { DifficultyLevel } from "@/api/difficulty-levels"

type Props = {
  onSessionCreated: (sessionId: string) => void
  onCancel: () => void
}

export default function CreateSessionView({ onSessionCreated, onCancel }: Props) {
  const { data: types, isLoading: typesLoading } = useInterviewTypes(true)
  const { data: categories, isLoading: catsLoading } = useCategories(true)
  const { data: difficulties, isLoading: diffLoading } = useDifficultyLevels()
  const { data: allQuestions } = useQuestions()

  const [selectedType, setSelectedType] = useState<InterviewType | null>(null)
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null)
  const [selectedDifficulty, setSelectedDifficulty] = useState<DifficultyLevel | null>(null)
  const [step, setStep] = useState(0)
  const [error, setError] = useState<string | null>(null)

  const createSession = useCreateInterviewSession()
  const createSQ = useCreateSessionQuestion()

  const handleCreate = async () => {
    if (!selectedType || !selectedCategory || !selectedDifficulty) return

    const filtered = (allQuestions ?? []).filter(
      (q) =>
        q.categoryId === selectedCategory.id &&
        q.difficultyId === selectedDifficulty.id &&
        q.isActive
    )

    if (filtered.length === 0) {
      setError("No hay preguntas disponibles para esta combinación de categoría y dificultad. Intenta con otra combinación.")
      return
    }

    setError(null)

    const res = await createSession.mutateAsync({
      status: "IN_PROGRESS",
      startedAt: new Date().toISOString(),
      interviewTypeId: selectedType.id,
      categoryId: selectedCategory.id,
      difficultyId: selectedDifficulty.id,
    })

    const sessionId = res.id

    const selected = filtered
      .sort(() => Math.random() - 0.5)
      .slice(0, selectedType.totalQuestions)

    for (let i = 0; i < selected.length; i++) {
      await createSQ.mutateAsync({
        questionOrder: i + 1,
        sessionId,
        questionId: selected[i].id,
        assignedTimeSeconds: selectedType.totalTimeSeconds,
      })
    }

    onSessionCreated(sessionId)
  }

  const loading = typesLoading || catsLoading || diffLoading

  if (loading) return <p className="text-muted-foreground">Cargando…</p>

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-bold">Nueva entrevista</h2>
        <button onClick={onCancel} className="text-sm text-muted-foreground hover:text-foreground transition-colors">
          Cancelar
        </button>
      </div>

      <div className="flex gap-2 mb-6">
        {["Tipo", "Categoría", "Dificultad"].map((_, i) => (
          <div key={i} className={`flex-1 h-1 rounded-full transition-colors ${i <= step ? "bg-neutral-900" : "bg-neutral-200"}`} />
        ))}
      </div>

      {step === 0 && (
        <div className="space-y-3">
          <p className="text-sm text-muted-foreground mb-4">Selecciona el tipo de entrevista</p>
          <div className="grid gap-3">
            {(types ?? []).map((t) => (
              <Card
                key={t.id}
                className={`cursor-pointer transition-all hover:border-neutral-400 ${
                  selectedType?.id === t.id ? "border-neutral-900 ring-1 ring-neutral-900" : ""
                }`}
                onClick={() => { setSelectedType(t); setError(null); setStep(1) }}
              >
                <CardContent className="pt-6">
                  <p className="font-semibold">{t.name}</p>
                  <p className="text-sm text-muted-foreground mt-1">
                    {t.totalQuestions} preguntas · {Math.floor(t.totalTimeSeconds / 60)} min · {t.questionType}
                  </p>
                  {t.description && <p className="text-xs text-muted-foreground mt-1">{t.description}</p>}
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      )}

      {step === 1 && (
        <div className="space-y-3">
          <p className="text-sm text-muted-foreground mb-4">Selecciona una categoría</p>
          <div className="grid gap-3">
            {(categories ?? []).map((c) => (
              <Card
                key={c.id}
                className={`cursor-pointer transition-all hover:border-neutral-400 ${
                  selectedCategory?.id === c.id ? "border-neutral-900 ring-1 ring-neutral-900" : ""
                }`}
                onClick={() => { setSelectedCategory(c); setError(null); setStep(2) }}
              >
                <CardContent className="pt-6">
                  <p className="font-semibold">{c.name}</p>
                  {c.description && <p className="text-sm text-muted-foreground mt-1">{c.description}</p>}
                </CardContent>
              </Card>
            ))}
          </div>
          <button onClick={() => setStep(0)} className="text-sm text-muted-foreground hover:text-foreground transition-colors">
            ← Volver
          </button>
        </div>
      )}

      {step === 2 && (
        <div className="space-y-3">
          <p className="text-sm text-muted-foreground mb-4">Selecciona el nivel de dificultad</p>
          <div className="grid gap-3">
            {(difficulties ?? []).map((d) => (
              <Card
                key={d.id}
                className={`cursor-pointer transition-all hover:border-neutral-400 ${
                  selectedDifficulty?.id === d.id ? "border-neutral-900 ring-1 ring-neutral-900" : ""
                }`}
                onClick={() => { setSelectedDifficulty(d); setError(null) }}
              >
                <CardContent className="pt-6">
                  <p className="font-semibold">{d.name}</p>
                  {d.description && <p className="text-sm text-muted-foreground mt-1">{d.description}</p>}
                </CardContent>
              </Card>
            ))}
          </div>
          {error && (
            <p className="text-sm text-red-500 bg-red-50 dark:bg-red-950/30 px-3 py-2 rounded-md">{error}</p>
          )}
          <div className="flex items-center justify-between pt-4">
            <button onClick={() => setStep(1)} className="text-sm text-muted-foreground hover:text-foreground transition-colors">
              ← Volver
            </button>
            <Button onClick={handleCreate} disabled={!selectedDifficulty || createSession.isPending}>
              {createSession.isPending ? "Creando..." : "Iniciar entrevista"}
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}
