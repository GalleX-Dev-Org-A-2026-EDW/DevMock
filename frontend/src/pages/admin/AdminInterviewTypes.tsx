import { useState } from "react"
import { useInterviewTypes, useCreateInterviewType, useUpdateInterviewType, useDeleteInterviewType } from "@/api/interview-types.queries"
import type { InterviewType, CreateInterviewTypeDto, UpdateInterviewTypeDto } from "@/api/interview-types"
import type { QuestionType } from "@/api/enums"
import { Plus, Edit2, Trash2, Eye, EyeOff } from "lucide-react"

export default function AdminInterviewTypes() {
  const { data: items, isLoading, isError } = useInterviewTypes()
  const createItem = useCreateInterviewType()
  const updateItem = useUpdateInterviewType()
  const deleteItem = useDeleteInterviewType()
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState<InterviewType | null>(null)
  const [confirmDelete, setConfirmDelete] = useState<string | null>(null)
  const [form, setForm] = useState<CreateInterviewTypeDto>({ name: "", slug: "", questionType: "THEORETICAL" as QuestionType, totalQuestions: 5, totalTimeSeconds: 1800 })

  const openCreate = () => { setEditing(null); setForm({ name: "", slug: "", questionType: "THEORETICAL", totalQuestions: 5, totalTimeSeconds: 1800 }); setShowModal(true) }
  const openEdit = (item: InterviewType) => { setEditing(item); setForm({ name: item.name, slug: item.slug, questionType: item.questionType, totalQuestions: item.totalQuestions, totalTimeSeconds: item.totalTimeSeconds, description: item.description ?? undefined }); setShowModal(true) }

  const handleSave = async () => {
    if (editing) { await updateItem.mutateAsync({ id: editing.id, dto: form as UpdateInterviewTypeDto }) }
    else { await createItem.mutateAsync(form) }
    setShowModal(false); setEditing(null)
  }

  const handleDelete = async (id: string) => { await deleteItem.mutateAsync(id); setConfirmDelete(null) }

  if (isLoading) return <div className="flex items-center justify-center h-full"><div className="h-8 w-8 border-2 border-emerald-500 border-t-transparent rounded-full animate-spin" /></div>
  if (isError) return <p className="text-red-400">Error al cargar tipos de entrevista</p>

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="font-['Work_Sans'] font-bold text-2xl text-white">Tipos de Entrevista</h1>
        <button onClick={openCreate} className="px-4 py-2 bg-emerald-500 text-white rounded-lg text-sm font-semibold hover:bg-emerald-600 transition-all flex items-center gap-2"><Plus className="h-4 w-4" /> Nuevo tipo</button>
      </div>
      <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-100 bg-gray-50/50">
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Nombre</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Slug</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Tipo preg.</th>
                <th className="text-center px-4 py-3 font-semibold text-gray-600">Preguntas</th>
                <th className="text-center px-4 py-3 font-semibold text-gray-600">Tiempo (min)</th>
                <th className="text-center px-4 py-3 font-semibold text-gray-600">Activo</th>
                <th className="text-right px-4 py-3 font-semibold text-gray-600">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {items?.map((item) => (
                <tr key={item.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                  <td className="px-4 py-3 font-medium text-gray-900">{item.name}</td>
                  <td className="px-4 py-3 text-gray-500 font-['JetBrains_Mono'] text-xs">{item.slug}</td>
                  <td className="px-4 py-3">
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${item.questionType === "PRACTICAL" ? "bg-blue-50 text-blue-700" : item.questionType === "MIXED" ? "bg-violet-50 text-violet-700" : "bg-amber-50 text-amber-700"}`}>
                      {item.questionType === "THEORETICAL" ? "Teórica" : item.questionType === "PRACTICAL" ? "Práctica" : "Mixta"}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-center text-gray-900">{item.totalQuestions}</td>
                  <td className="px-4 py-3 text-center text-gray-900">{Math.round(item.totalTimeSeconds / 60)}</td>
                  <td className="px-4 py-3 text-center">{item.isActive ? <Eye className="h-4 w-4 text-emerald-500 mx-auto" /> : <EyeOff className="h-4 w-4 text-gray-300 mx-auto" />}</td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button onClick={() => openEdit(item)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors"><Edit2 className="h-4 w-4 text-gray-400" /></button>
                      <button onClick={() => setConfirmDelete(item.id)} className="p-1.5 rounded-lg hover:bg-red-50 transition-colors"><Trash2 className="h-4 w-4 text-red-400" /></button>
                    </div>
                  </td>
                </tr>
              ))}
              {(!items || items.length === 0) && <tr><td colSpan={7} className="px-4 py-8 text-center text-gray-400">No hay tipos de entrevista</td></tr>}
            </tbody>
          </table>
        </div>
      </div>
      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-6" onClick={() => setShowModal(false)}>
          <div className="bg-white rounded-2xl p-8 w-full max-w-md shadow-2xl" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-['Work_Sans'] font-bold text-xl text-gray-900 mb-6">{editing ? "Editar tipo" : "Nuevo tipo"}</h2>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
                  <input type="text" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Slug</label>
                  <input type="text" value={form.slug} onChange={(e) => setForm({ ...form, slug: e.target.value })} className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Tipo de pregunta</label>
                <select value={form.questionType} onChange={(e) => setForm({ ...form, questionType: e.target.value as QuestionType })} className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary">
                  <option value="THEORETICAL">Teórica</option>
                  <option value="PRACTICAL">Práctica</option>
                  <option value="MIXED">Mixta</option>
                </select>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Total preguntas</label>
                  <input type="number" value={form.totalQuestions} onChange={(e) => setForm({ ...form, totalQuestions: parseInt(e.target.value) || 0 })} className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Tiempo total (segundos)</label>
                  <input type="number" value={form.totalTimeSeconds} onChange={(e) => setForm({ ...form, totalTimeSeconds: parseInt(e.target.value) || 0 })} className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
                <textarea value={form.description || ""} onChange={(e) => setForm({ ...form, description: e.target.value })} rows={3} className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary" />
              </div>
              <div className="flex gap-3 pt-2">
                <button onClick={() => setShowModal(false)} className="flex-1 px-4 py-2.5 border border-gray-200 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-all">Cancelar</button>
                <button onClick={handleSave} disabled={createItem.isPending || updateItem.isPending} className="flex-1 px-4 py-2.5 bg-primary text-primary-foreground rounded-lg text-sm font-semibold hover:bg-primary/90 transition-all disabled:opacity-50">{editing ? "Guardar" : "Crear"}</button>
              </div>
            </div>
          </div>
        </div>
      )}
      {confirmDelete && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-6" onClick={() => setConfirmDelete(null)}>
          <div className="bg-white rounded-2xl p-8 w-full max-w-sm shadow-2xl" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-['Work_Sans'] font-bold text-xl text-gray-900 mb-2">¿Eliminar tipo?</h2>
            <p className="text-sm text-gray-500 mb-6">Esta acción no se puede deshacer.</p>
            <div className="flex gap-3">
              <button onClick={() => setConfirmDelete(null)} className="flex-1 px-4 py-2.5 border border-gray-200 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-all">Cancelar</button>
              <button onClick={() => handleDelete(confirmDelete)} disabled={deleteItem.isPending} className="flex-1 px-4 py-2.5 bg-red-500 text-white rounded-lg text-sm font-semibold hover:bg-red-600 transition-all disabled:opacity-50">Eliminar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
