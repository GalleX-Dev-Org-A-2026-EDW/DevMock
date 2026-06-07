import { useState } from "react"
import { useQuestions, useCreateQuestion, useUpdateQuestion, useDeleteQuestion } from "@/api/questions.queries"
import { useCategories } from "@/api/categories.queries"
import { useDifficultyLevels } from "@/api/difficulty-levels.queries"
import type { Question, CreateQuestionDto, UpdateQuestionDto } from "@/api/questions"
import type { QuestionType, AnswerFormat } from "@/api/enums"
import { Plus, Edit2, Trash2, Eye, EyeOff } from "lucide-react"

export default function AdminQuestions() {
  const { data: questions, isLoading, isError } = useQuestions()
  const { data: categories } = useCategories()
  const { data: difficultyLevels } = useDifficultyLevels()
  const createQuestion = useCreateQuestion()
  const updateQuestion = useUpdateQuestion()
  const deleteQuestion = useDeleteQuestion()

  const [showModal, setShowModal] = useState(false)
  const [editingQuestion, setEditingQuestion] = useState<Question | null>(null)
  const [confirmDelete, setConfirmDelete] = useState<string | null>(null)
  const [expandedId, setExpandedId] = useState<string | null>(null)

  const [form, setForm] = useState<CreateQuestionDto>({
    questionType: "THEORETICAL" as QuestionType,
    answerFormat: "FREE_TEXT" as AnswerFormat,
    statement: "",
    basePoints: 10,
    tags: [],
  })

  const [tagInput, setTagInput] = useState("")

  const openCreate = () => {
    setEditingQuestion(null)
    setForm({ questionType: "THEORETICAL", answerFormat: "FREE_TEXT", statement: "", basePoints: 10, tags: [] })
    setShowModal(true)
  }

  const openEdit = (q: Question) => {
    setEditingQuestion(q)
    setForm({
      questionType: q.questionType,
      answerFormat: q.answerFormat,
      statement: q.statement,
      expectedAnswer: q.expectedAnswer ?? undefined,
      explanation: q.explanation ?? undefined,
      estimatedTimeSeconds: q.estimatedTimeSeconds ?? undefined,
      basePoints: q.basePoints,
      tags: q.tags,
      categoryId: q.categoryId ?? undefined,
      difficultyId: q.difficultyId ?? undefined,
    })
    setShowModal(true)
  }

  const handleSave = async () => {
    if (editingQuestion) {
      const dto: UpdateQuestionDto = {
        questionType: form.questionType,
        answerFormat: form.answerFormat,
        statement: form.statement,
        basePoints: form.basePoints,
        tags: form.tags,
      }
      if (form.expectedAnswer) dto.expectedAnswer = form.expectedAnswer
      if (form.explanation) dto.explanation = form.explanation
      if (form.estimatedTimeSeconds) dto.estimatedTimeSeconds = form.estimatedTimeSeconds
      if (form.categoryId) dto.categoryId = form.categoryId
      if (form.difficultyId) dto.difficultyId = form.difficultyId
      await updateQuestion.mutateAsync({ id: editingQuestion.id, dto })
    } else {
      await createQuestion.mutateAsync(form)
    }
    setShowModal(false)
    setEditingQuestion(null)
  }

  const handleDelete = async (id: string) => {
    await deleteQuestion.mutateAsync(id)
    setConfirmDelete(null)
  }

  const addTag = () => {
    const tag = tagInput.trim()
    if (tag && !form.tags?.includes(tag)) {
      setForm({ ...form, tags: [...(form.tags || []), tag] })
    }
    setTagInput("")
  }

  const removeTag = (tag: string) => {
    setForm({ ...form, tags: form.tags?.filter((t) => t !== tag) })
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="h-8 w-8 border-2 border-emerald-500 border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  if (isError) {
    return <p className="text-red-400">Error al cargar preguntas</p>
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="font-['Work_Sans'] font-bold text-2xl text-white">Preguntas</h1>
        <button
          onClick={openCreate}
          className="px-4 py-2 bg-emerald-500 text-white rounded-lg text-sm font-semibold hover:bg-emerald-600 transition-all flex items-center gap-2"
        >
          <Plus className="h-4 w-4" /> Nueva pregunta
        </button>
      </div>

      <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-100 bg-gray-50/50">
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Enunciado</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Tipo</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Formato</th>
                <th className="text-center px-4 py-3 font-semibold text-gray-600">Puntos</th>
                <th className="text-center px-4 py-3 font-semibold text-gray-600">Activa</th>
                <th className="text-right px-4 py-3 font-semibold text-gray-600">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {questions?.map((q) => (
                <tr key={q.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                  <td className="px-4 py-3 max-w-xs">
                    <button
                      onClick={() => setExpandedId(expandedId === q.id ? null : q.id)}
                      className="text-left"
                    >
                      <span className="text-gray-900 font-medium line-clamp-2">{q.statement}</span>
                    </button>
                    {expandedId === q.id && q.expectedAnswer && (
                      <div className="mt-2 p-3 bg-gray-50 rounded-lg text-xs text-gray-600 font-['JetBrains_Mono']">
                        <span className="font-semibold text-gray-700">Respuesta:</span> {q.expectedAnswer}
                      </div>
                    )}
                    {q.tags.length > 0 && (
                      <div className="flex flex-wrap gap-1 mt-1">
                        {q.tags.map((tag) => (
                          <span key={tag} className="text-xs px-1.5 py-0.5 bg-gray-100 text-gray-500 rounded">{tag}</span>
                        ))}
                      </div>
                    )}
                  </td>
                  <td className="px-4 py-3">
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                      q.questionType === "PRACTICAL" ? "bg-blue-50 text-blue-700" :
                      q.questionType === "MIXED" ? "bg-violet-50 text-violet-700" :
                      "bg-amber-50 text-amber-700"
                    }`}>
                      {q.questionType === "THEORETICAL" ? "Teórica" : q.questionType === "PRACTICAL" ? "Práctica" : "Mixta"}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-gray-500">
                    {q.answerFormat === "FREE_TEXT" ? "Texto libre" :
                     q.answerFormat === "CODE" ? "Código" :
                     q.answerFormat === "MULTIPLE_CHOICE" ? "Opción múltiple" : "Opción única"}
                  </td>
                  <td className="px-4 py-3 text-center font-medium text-gray-900">{q.basePoints}</td>
                  <td className="px-4 py-3 text-center">
                    {q.isActive ? <Eye className="h-4 w-4 text-emerald-500 mx-auto" /> : <EyeOff className="h-4 w-4 text-gray-300 mx-auto" />}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button onClick={() => openEdit(q)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                        <Edit2 className="h-4 w-4 text-gray-400" />
                      </button>
                      <button onClick={() => setConfirmDelete(q.id)} className="p-1.5 rounded-lg hover:bg-red-50 transition-colors">
                        <Trash2 className="h-4 w-4 text-red-400" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {(!questions || questions.length === 0) && (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-gray-400">No hay preguntas registradas</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-6" onClick={() => setShowModal(false)}>
          <div className="bg-white rounded-2xl p-8 w-full max-w-2xl shadow-2xl max-h-[90vh] overflow-y-auto" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-['Work_Sans'] font-bold text-xl text-gray-900 mb-6">
              {editingQuestion ? "Editar pregunta" : "Nueva pregunta"}
            </h2>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Tipo</label>
                  <select
                    value={form.questionType}
                    onChange={(e) => setForm({ ...form, questionType: e.target.value as QuestionType })}
                    className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                  >
                    <option value="THEORETICAL">Teórica</option>
                    <option value="PRACTICAL">Práctica</option>
                    <option value="MIXED">Mixta</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Formato respuesta</label>
                  <select
                    value={form.answerFormat}
                    onChange={(e) => setForm({ ...form, answerFormat: e.target.value as AnswerFormat })}
                    className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                  >
                    <option value="FREE_TEXT">Texto libre</option>
                    <option value="CODE">Código</option>
                    <option value="MULTIPLE_CHOICE">Opción múltiple</option>
                    <option value="SINGLE_CHOICE">Opción única</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Enunciado</label>
                <textarea
                  value={form.statement}
                  onChange={(e) => setForm({ ...form, statement: e.target.value })}
                  rows={4}
                  className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Respuesta esperada</label>
                <textarea
                  value={form.expectedAnswer || ""}
                  onChange={(e) => setForm({ ...form, expectedAnswer: e.target.value })}
                  rows={3}
                  className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Explicación</label>
                <textarea
                  value={form.explanation || ""}
                  onChange={(e) => setForm({ ...form, explanation: e.target.value })}
                  rows={2}
                  className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Puntos base</label>
                  <input
                    type="number"
                    value={form.basePoints}
                    onChange={(e) => setForm({ ...form, basePoints: parseInt(e.target.value) || 0 })}
                    className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Tiempo estimado (seg)</label>
                  <input
                    type="number"
                    value={form.estimatedTimeSeconds || ""}
                    onChange={(e) => setForm({ ...form, estimatedTimeSeconds: e.target.value ? parseInt(e.target.value) : undefined })}
                    className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Categoría</label>
                  <select
                    value={form.categoryId || ""}
                    onChange={(e) => setForm({ ...form, categoryId: e.target.value || undefined })}
                    className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                  >
                    <option value="">Sin categoría</option>
                    {categories?.map((cat) => (
                      <option key={cat.id} value={cat.id}>{cat.name}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Dificultad</label>
                  <select
                    value={form.difficultyId || ""}
                    onChange={(e) => setForm({ ...form, difficultyId: e.target.value || undefined })}
                    className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                  >
                    <option value="">Sin dificultad</option>
                    {difficultyLevels?.map((dl) => (
                      <option key={dl.id} value={dl.id}>{dl.name}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Tags</label>
                <div className="flex gap-2 mb-2">
                  <input
                    type="text"
                    value={tagInput}
                    onChange={(e) => setTagInput(e.target.value)}
                    onKeyDown={(e) => e.key === "Enter" && (e.preventDefault(), addTag())}
                    placeholder="Agregar tag..."
                    className="flex-1 px-3 py-2 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                  />
                  <button onClick={addTag} className="px-3 py-2 bg-gray-100 text-gray-700 rounded-lg text-sm hover:bg-gray-200 transition-colors">
                    Agregar
                  </button>
                </div>
                {form.tags && form.tags.length > 0 && (
                  <div className="flex flex-wrap gap-1.5">
                    {form.tags.map((tag) => (
                      <span key={tag} className="inline-flex items-center gap-1 px-2 py-1 bg-gray-100 text-gray-600 rounded-md text-xs">
                        {tag}
                        <button onClick={() => removeTag(tag)} className="hover:text-red-500">&times;</button>
                      </span>
                    ))}
                  </div>
                )}
              </div>

              <div className="flex gap-3 pt-2">
                <button
                  onClick={() => setShowModal(false)}
                  className="flex-1 px-4 py-2.5 border border-gray-200 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-all"
                >
                  Cancelar
                </button>
                <button
                  onClick={handleSave}
                  disabled={createQuestion.isPending || updateQuestion.isPending}
                  className="flex-1 px-4 py-2.5 bg-primary text-primary-foreground rounded-lg text-sm font-semibold hover:bg-primary/90 transition-all disabled:opacity-50"
                >
                  {editingQuestion ? "Guardar" : "Crear"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {confirmDelete && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-6" onClick={() => setConfirmDelete(null)}>
          <div className="bg-white rounded-2xl p-8 w-full max-w-sm shadow-2xl" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-['Work_Sans'] font-bold text-xl text-gray-900 mb-2">¿Eliminar pregunta?</h2>
            <p className="text-sm text-gray-500 mb-6">Esta acción no se puede deshacer.</p>
            <div className="flex gap-3">
              <button
                onClick={() => setConfirmDelete(null)}
                className="flex-1 px-4 py-2.5 border border-gray-200 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-all"
              >
                Cancelar
              </button>
              <button
                onClick={() => handleDelete(confirmDelete)}
                disabled={deleteQuestion.isPending}
                className="flex-1 px-4 py-2.5 bg-red-500 text-white rounded-lg text-sm font-semibold hover:bg-red-600 transition-all disabled:opacity-50"
              >
                Eliminar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
