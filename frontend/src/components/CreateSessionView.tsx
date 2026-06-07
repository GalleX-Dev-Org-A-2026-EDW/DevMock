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
  const [error, setError] = useState("")

  const createSession = useCreateInterviewSession()
  const createSQ = useCreateSessionQuestion()

  const handleCreate = async () => {
    if (!selectedType || !selectedCategory || !selectedDifficulty) return

    setError("")

    const filtered = (allQuestions ?? []).filter(
      (q) =>
        q.categoryId === selectedCategory.id &&
        q.difficultyId === selectedDifficulty.id &&
        q.isActive &&
        (selectedType.questionType === "MIXED" || q.questionType === selectedType.questionType),
    )

    const selected = filtered
      .sort(() => Math.random() - 0.5)
      .slice(0, selectedType.totalQuestions)

    if (selected.length === 0) {
      setError("No hay preguntas disponibles para esta combinación. Prueba otra categoría o dificultad.")
      return
    }

    const res = await createSession.mutateAsync({
      status: "IN_PROGRESS",
      startedAt: new Date().toISOString(),
      interviewTypeId: selectedType.id,
      categoryId: selectedCategory.id,
      difficultyId: selectedDifficulty.id,
    })

    const sessionId = res.id
    const fallbackTime = Math.floor(selectedType.totalTimeSeconds / selected.length)

    for (let i = 0; i < selected.length; i++) {
      await createSQ.mutateAsync({
        questionOrder: i + 1,
        sessionId,
        questionId: selected[i].id,
        assignedTimeSeconds: selected[i].estimatedTimeSeconds ?? fallbackTime,
      })
    }

    onSessionCreated(sessionId)
  }

  const loading = typesLoading || catsLoading || diffLoading
  const creating = createSession.isPending || createSQ.isPending

  if (loading) return <p className="text-sm text-white/60">Cargando...</p>

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-bold text-white">Nueva entrevista</h2>
        <button onClick={onCancel} className="text-sm text-white/60 transition-colors hover:text-white">
          Cancelar
        </button>
      </div>

      <div className="mb-6 flex gap-2">
        {["Tipo", "Categoría", "Dificultad"].map((_, i) => (
          <div key={i} className={`h-1 flex-1 rounded-full transition-colors ${i <= step ? "bg-white" : "bg-white/20"}`} />
        ))}
      </div>

      {step === 0 && (
        <div className="space-y-3">
          <p className="mb-4 text-sm text-white/60">Selecciona el tipo de entrevista</p>
          <div className="grid gap-3">
            {(types ?? []).map((t) => (
              <Card
                key={t.id}
                className={`cursor-pointer transition-all hover:border-neutral-400 ${
                  selectedType?.id === t.id ? "border-neutral-900 ring-1 ring-neutral-900" : ""
                }`}
                onClick={() => {
                  setSelectedType(t)
                  setStep(1)
                  setError("")
                }}
              >
                <CardContent className="pt-6">
                  <p className="font-semibold">{t.name}</p>
                  <p className="mt-1 text-sm text-muted-foreground">
                    {t.totalQuestions} preguntas · {Math.floor(t.totalTimeSeconds / 60)} min · {t.questionType}
                  </p>
                  {t.description && <p className="mt-1 text-xs text-muted-foreground">{t.description}</p>}
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      )}

      {step === 1 && (
        <div className="space-y-3">
          <p className="mb-4 text-sm text-white/60">Selecciona una categoría</p>
          <div className="grid gap-3">
            {(categories ?? []).map((c) => (
              <Card
                key={c.id}
                className={`cursor-pointer transition-all hover:border-neutral-400 ${
                  selectedCategory?.id === c.id ? "border-neutral-900 ring-1 ring-neutral-900" : ""
                }`}
                onClick={() => {
                  setSelectedCategory(c)
                  setStep(2)
                  setError("")
                }}
              >
                <CardContent className="pt-6">
                  <p className="font-semibold">{c.name}</p>
                  {c.description && <p className="mt-1 text-sm text-white/60">{c.description}</p>}
                </CardContent>
              </Card>
            ))}
          </div>
          <button onClick={() => setStep(0)} className="text-sm text-white/60 transition-colors hover:text-white">
            Volver
          </button>
        </div>
      )}

      {step === 2 && (
        <div className="space-y-3">
          <p className="mb-4 text-sm text-white/60">Selecciona el nivel de dificultad</p>
          {error && (
            <div className="rounded-lg border border-red-500/30 bg-red-500/10 px-4 py-3 text-sm text-red-400">
              {error}
            </div>
          )}
          <div className="grid gap-3">
            {(difficulties ?? []).map((d) => (
              <Card
                key={d.id}
                className={`cursor-pointer transition-all hover:border-neutral-400 ${
                  selectedDifficulty?.id === d.id ? "border-neutral-900 ring-1 ring-neutral-900" : ""
                }`}
                onClick={() => {
                  setSelectedDifficulty(d)
                  setError("")
                }}
              >
                <CardContent className="pt-6">
                  <p className="font-semibold">{d.name}</p>
                  {d.description && <p className="mt-1 text-sm text-white/60">{d.description}</p>}
                </CardContent>
              </Card>
            ))}
          </div>
          <div className="flex items-center justify-between pt-4">
            <button onClick={() => setStep(1)} className="text-sm text-white/60 transition-colors hover:text-white">
              Volver
            </button>
            <Button onClick={handleCreate} disabled={!selectedDifficulty || creating}>
              {creating ? "Creando..." : "Iniciar entrevista"}
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}
