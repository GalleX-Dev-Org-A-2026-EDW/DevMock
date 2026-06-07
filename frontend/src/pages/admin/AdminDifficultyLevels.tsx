import { useState } from "react"
import { useDifficultyLevels, useCreateDifficultyLevel, useUpdateDifficultyLevel, useDeleteDifficultyLevel } from "@/api/difficulty-levels.queries"
import type { DifficultyLevel, CreateDifficultyLevelDto, UpdateDifficultyLevelDto } from "@/api/difficulty-levels"
import { Plus, Edit2, Trash2 } from "lucide-react"

export default function AdminDifficultyLevels() {
  const { data: items, isLoading, isError } = useDifficultyLevels()
  const createItem = useCreateDifficultyLevel()
  const updateItem = useUpdateDifficultyLevel()
  const deleteItem = useDeleteDifficultyLevel()
  const [showModal, setShowModal] = useState(false)
  const [editing, setEditing] = useState<DifficultyLevel | null>(null)
  const [confirmDelete, setConfirmDelete] = useState<string | null>(null)
  const [form, setForm] = useState<CreateDifficultyLevelDto>({ name: "", slug: "", levelOrder: 1, pointsMultiplier: 1.0 })
  const [errors, setErrors] = useState<Record<string, string>>({})

  const validate = (): boolean => {
    const next: Record<string, string> = {}
    if (!form.name.trim()) next.name = "El nombre es obligatorio"
    if (!form.slug.trim()) next.slug = "El slug es obligatorio"
    if (!form.levelOrder || form.levelOrder <= 0) next.levelOrder = "El orden debe ser mayor que 0"
    if (!form.pointsMultiplier || form.pointsMultiplier <= 0) next.pointsMultiplier = "El multiplicador debe ser mayor que 0"
    setErrors(next)
    return Object.keys(next).length === 0
  }

  const clearError = (field: string) => {
    setErrors((prev) => { const n = { ...prev }; delete n[field]; return n })
  }

  const closeModal = () => { setShowModal(false); setEditing(null); setErrors({}) }

  const openCreate = () => { setErrors({}); setEditing(null); setForm({ name: "", slug: "", levelOrder: 1, pointsMultiplier: 1.0 }); setShowModal(true) }
  const openEdit = (item: DifficultyLevel) => { setErrors({}); setEditing(item); setForm({ name: item.name, slug: item.slug, levelOrder: item.levelOrder, pointsMultiplier: item.pointsMultiplier, description: item.description ?? undefined }); setShowModal(true) }

  const handleSave = async () => {
    if (!validate()) return
    if (editing) { await updateItem.mutateAsync({ id: editing.id, dto: form as UpdateDifficultyLevelDto }) }
    else { await createItem.mutateAsync(form) }
    setShowModal(false); setEditing(null); setErrors({})
  }

  const handleDelete = async (id: string) => { await deleteItem.mutateAsync(id); setConfirmDelete(null) }

  if (isLoading) return <div className="flex items-center justify-center h-full"><div className="h-8 w-8 border-2 border-emerald-500 border-t-transparent rounded-full animate-spin" /></div>
  if (isError) return <p className="text-red-400">Error al cargar niveles de dificultad</p>

  return (
    <div>
      <div className="flex items-center justify-between flex-wrap gap-2 mb-6">
        <h1 className="font-['Work_Sans'] font-bold text-xl sm:text-2xl text-white">Niveles de Dificultad</h1>
        <button onClick={openCreate} className="px-4 py-2 bg-emerald-500 text-white rounded-lg text-sm font-semibold hover:bg-emerald-600 transition-all flex items-center gap-2"><Plus className="h-4 w-4" /> Nuevo nivel</button>
      </div>
      <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-100 bg-gray-50/50">
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Nombre</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Slug</th>
                <th className="text-center px-4 py-3 font-semibold text-gray-600">Orden</th>
                <th className="text-center px-4 py-3 font-semibold text-gray-600">Multiplicador</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Descripción</th>
                <th className="text-right px-4 py-3 font-semibold text-gray-600">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {items?.map((item) => (
                <tr key={item.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                  <td className="px-4 py-3 font-medium text-gray-900">{item.name}</td>
                  <td className="px-4 py-3 text-gray-500 font-['JetBrains_Mono'] text-xs">{item.slug}</td>
                  <td className="px-4 py-3 text-center text-gray-900">{item.levelOrder}</td>
                  <td className="px-4 py-3 text-center font-['JetBrains_Mono'] text-gray-900">{item.pointsMultiplier}x</td>
                  <td className="px-4 py-3 text-gray-500 max-w-xs truncate">{item.description}</td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button onClick={() => openEdit(item)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors"><Edit2 className="h-4 w-4 text-gray-400" /></button>
                      <button onClick={() => setConfirmDelete(item.id)} className="p-1.5 rounded-lg hover:bg-red-50 transition-colors"><Trash2 className="h-4 w-4 text-red-400" /></button>
                    </div>
                  </td>
                </tr>
              ))}
              {(!items || items.length === 0) && <tr><td colSpan={6} className="px-4 py-8 text-center text-gray-400">No hay niveles de dificultad</td></tr>}
            </tbody>
          </table>
        </div>
      </div>
      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-6" onClick={closeModal}>
          <div className="bg-white rounded-2xl p-8 w-full max-w-md shadow-2xl" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-['Work_Sans'] font-bold text-xl text-gray-900 mb-6">{editing ? "Editar nivel" : "Nuevo nivel"}</h2>
            <div className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Nombre <span className="text-red-400">*</span></label>
                  <input type="text" value={form.name} onChange={(e) => { setForm({ ...form, name: e.target.value }); clearError("name") }} className={`w-full px-3 py-2.5 rounded-lg border text-sm focus:outline-none focus:ring-2 focus:ring-primary ${errors.name ? "border-red-400" : "border-gray-200"}`} />
                  {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Slug <span className="text-red-400">*</span></label>
                  <input type="text" value={form.slug} onChange={(e) => { setForm({ ...form, slug: e.target.value }); clearError("slug") }} className={`w-full px-3 py-2.5 rounded-lg border text-sm focus:outline-none focus:ring-2 focus:ring-primary ${errors.slug ? "border-red-400" : "border-gray-200"}`} />
                  {errors.slug && <p className="text-red-500 text-xs mt-1">{errors.slug}</p>}
                </div>
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Orden <span className="text-red-400">*</span></label>
                  <input type="number" value={form.levelOrder} onChange={(e) => { setForm({ ...form, levelOrder: parseInt(e.target.value) || 0 }); clearError("levelOrder") }} className={`w-full px-3 py-2.5 rounded-lg border text-sm focus:outline-none focus:ring-2 focus:ring-primary ${errors.levelOrder ? "border-red-400" : "border-gray-200"}`} />
                  {errors.levelOrder && <p className="text-red-500 text-xs mt-1">{errors.levelOrder}</p>}
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Multiplicador de puntos <span className="text-red-400">*</span></label>
                  <input type="number" step="0.1" value={form.pointsMultiplier} onChange={(e) => { setForm({ ...form, pointsMultiplier: parseFloat(e.target.value) || 1 }); clearError("pointsMultiplier") }} className={`w-full px-3 py-2.5 rounded-lg border text-sm focus:outline-none focus:ring-2 focus:ring-primary ${errors.pointsMultiplier ? "border-red-400" : "border-gray-200"}`} />
                  {errors.pointsMultiplier && <p className="text-red-500 text-xs mt-1">{errors.pointsMultiplier}</p>}
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Descripción</label>
                <textarea value={form.description || ""} onChange={(e) => setForm({ ...form, description: e.target.value })} rows={3} className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary" />
              </div>
              <div className="flex gap-3 pt-2">
                <button onClick={closeModal} className="flex-1 px-4 py-2.5 border border-gray-200 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-all">Cancelar</button>
                <button onClick={handleSave} disabled={createItem.isPending || updateItem.isPending} className="flex-1 px-4 py-2.5 bg-primary text-primary-foreground rounded-lg text-sm font-semibold hover:bg-primary/90 transition-all disabled:opacity-50">{editing ? "Guardar" : "Crear"}</button>
              </div>
            </div>
          </div>
        </div>
      )}
      {confirmDelete && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-6" onClick={() => setConfirmDelete(null)}>
          <div className="bg-white rounded-2xl p-8 w-full max-w-sm shadow-2xl" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-['Work_Sans'] font-bold text-xl text-gray-900 mb-2">¿Eliminar nivel?</h2>
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
