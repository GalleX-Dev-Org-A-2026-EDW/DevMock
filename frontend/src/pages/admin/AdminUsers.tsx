import { useState } from "react"
import { useUsers, useCreateUser, useUpdateUser, useDeleteUser } from "@/api/users.queries"
import type { User, CreateUserDto, UpdateUserDto } from "@/api/users"
import type { UserRole } from "@/api/enums"
import { Plus, Edit2, Trash2, UserCheck, UserX, Shield } from "lucide-react"

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function getPageRange(current: number, total: number): (number | "...")[] {
  if (total <= 7) return Array.from({ length: total }, (_, i) => i + 1)
  const pages: (number | "...")[] = [1]
  if (current > 3) pages.push("...")
  const start = Math.max(2, current - 1)
  const end = Math.min(total - 1, current + 1)
  for (let i = start; i <= end; i++) pages.push(i)
  if (current < total - 2) pages.push("...")
  if (total > 1) pages.push(total)
  return pages
}

const PAGE_SIZE = 10

export default function AdminUsers() {
  const { data: users, isLoading, isError } = useUsers()
  const createUser = useCreateUser()
  const updateUser = useUpdateUser()
  const deleteUser = useDeleteUser()

  const [showModal, setShowModal] = useState(false)
  const [editingUser, setEditingUser] = useState<User | null>(null)
  const [confirmDelete, setConfirmDelete] = useState<string | null>(null)
  const [errors, setErrors] = useState<Record<string, string>>({})
  const [page, setPage] = useState(1)

  const totalPages = users ? Math.max(1, Math.ceil(users.length / PAGE_SIZE)) : 1
  const currentPage = Math.min(page, totalPages)
  const paginatedUsers = users?.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE)

  const [form, setForm] = useState<CreateUserDto>({
    email: "",
    password: "",
    fullName: "",
    role: "STUDENT" as UserRole,
  })

  const validate = (): boolean => {
    const next: Record<string, string> = {}
    if (!form.fullName.trim()) next.fullName = "El nombre es obligatorio"
    if (!form.email.trim()) {
      next.email = "El email es obligatorio"
    } else if (!EMAIL_RE.test(form.email.trim())) {
      next.email = "Ingrese un email válido"
    }
    if (!editingUser) {
      if (!form.password) next.password = "La contraseña es obligatoria"
      else if (form.password.length < 8) next.password = "Mínimo 8 caracteres"
    } else if (form.password && form.password.length < 8) {
      next.password = "Mínimo 8 caracteres"
    }
    setErrors(next)
    return Object.keys(next).length === 0
  }

  const clearError = (field: string) => {
    setErrors((prev) => { const n = { ...prev }; delete n[field]; return n })
  }

  const closeModal = () => { setShowModal(false); setEditingUser(null); setErrors({}) }

  const openCreate = () => {
    setEditingUser(null)
    setForm({ email: "", password: "", fullName: "", role: "STUDENT" })
    setErrors({})
    setShowModal(true)
  }

  const openEdit = (user: User) => {
    setEditingUser(user)
    setForm({
      email: user.email,
      password: "",
      fullName: user.fullName,
      role: user.role,
      avatarUrl: user.avatarUrl ?? undefined,
      professionalExperienceYears: user.professionalExperienceYears ?? undefined,
    })
    setErrors({})
    setShowModal(true)
  }

  const handleSave = async () => {
    if (!validate()) return
    if (editingUser) {
      const dto: UpdateUserDto = {
        fullName: form.fullName,
        role: form.role as UserRole,
      }
      if (form.password) dto.password = form.password
      if (form.avatarUrl) dto.avatarUrl = form.avatarUrl
      if (form.professionalExperienceYears !== undefined) dto.professionalExperienceYears = form.professionalExperienceYears
      await updateUser.mutateAsync({ id: editingUser.id, dto })
    } else {
      await createUser.mutateAsync(form)
    }
    setShowModal(false)
    setEditingUser(null)
    setErrors({})
  }

  const handleDelete = async (id: string) => {
    await deleteUser.mutateAsync(id)
    setConfirmDelete(null)
  }

  const toggleActive = async (user: User) => {
    await updateUser.mutateAsync({ id: user.id, dto: { isActive: !user.isActive } })
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="h-8 w-8 border-2 border-emerald-400 border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  if (isError) {
    return <p className="text-red-400">Error al cargar usuarios</p>
  }

  return (
    <div>
      <div className="flex items-center flex-wrap gap-3 mb-6">
        <div className="h-7 w-1 rounded-full bg-gradient-to-b from-emerald-400 to-emerald-600" />
        <h1 className="font-['Work_Sans'] font-bold text-xl sm:text-2xl text-white">Usuarios</h1>
        <div className="ml-auto">
          <button
            onClick={openCreate}
            className="px-4 py-2 bg-emerald-500 text-white rounded-lg text-sm font-semibold hover:bg-emerald-600 transition-all flex items-center gap-2"
          >
            <Plus className="h-4 w-4" /> Nuevo usuario
          </button>
        </div>
      </div>

      <div className="bg-white/[0.04] backdrop-blur-sm rounded-xl border border-white/[0.06] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-white/[0.06] bg-white/[0.03]">
                <th className="text-left px-4 py-3 font-semibold text-white/50">Nombre</th>
                <th className="text-left px-4 py-3 font-semibold text-white/50">Email</th>
                <th className="text-left px-4 py-3 font-semibold text-white/50">Rol</th>
                <th className="text-center px-4 py-3 font-semibold text-white/50">Activo</th>
                <th className="text-center px-4 py-3 font-semibold text-white/50">Verificado</th>
                <th className="text-right px-4 py-3 font-semibold text-white/50">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {paginatedUsers?.map((user) => (
                <tr key={user.id} className="border-b border-white/[0.04] hover:bg-white/[0.03] transition-colors">
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-3">
                      <div className="h-8 w-8 rounded-full bg-gradient-to-br from-emerald-400 to-blue-500 flex items-center justify-center text-white text-xs font-bold">
                        {user.fullName.charAt(0).toUpperCase()}
                      </div>
                      <span className="font-medium text-white/90">{user.fullName}</span>
                    </div>
                  </td>
                  <td className="px-4 py-3 text-white/50">{user.email}</td>
                  <td className="px-4 py-3">
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                      user.role === "ADMIN" ? "bg-rose-500/10 text-rose-300 ring-1 ring-inset ring-rose-500/20" :
                      user.role === "PROFESSIONAL" ? "bg-violet-500/10 text-violet-300 ring-1 ring-inset ring-violet-500/20" :
                      "bg-emerald-500/10 text-emerald-300 ring-1 ring-inset ring-emerald-500/20"
                    }`}>
                      {user.role === "STUDENT" ? "Estudiante" : user.role === "PROFESSIONAL" ? "Profesional" : "Admin"}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-center">
                    <button onClick={() => toggleActive(user)} className="transition-colors">
                      {user.isActive ? <UserCheck className="h-5 w-5 text-emerald-400" /> : <UserX className="h-5 w-5 text-red-400" />}
                    </button>
                  </td>
                  <td className="px-4 py-3 text-center">
                    {user.isVerified ? <Shield className="h-5 w-5 text-emerald-400 mx-auto" /> : <Shield className="h-5 w-5 text-white/20 mx-auto" />}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button onClick={() => openEdit(user)} className="p-1.5 rounded-lg hover:bg-white/[0.08] transition-colors">
                        <Edit2 className="h-4 w-4 text-white/40" />
                      </button>
                      <button onClick={() => setConfirmDelete(user.id)} className="p-1.5 rounded-lg hover:bg-red-500/10 transition-colors">
                        <Trash2 className="h-4 w-4 text-red-400" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {(!users || users.length === 0) && (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-white/40">No hay usuarios registrados</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {users && users.length > PAGE_SIZE && (
        <div className="flex items-center justify-between mt-4 px-1">
          <p className="text-sm text-white/40">
            {users.length} usuarios — Página {currentPage} de {totalPages}
          </p>
          <div className="flex items-center gap-2">
            <button
              onClick={() => setPage((p) => Math.max(1, p - 1))}
              disabled={currentPage <= 1}
              className="px-3 py-1.5 rounded-lg text-sm font-medium bg-white/[0.04] text-white/60 hover:bg-white/[0.08] disabled:opacity-30 disabled:cursor-not-allowed transition-all border border-white/[0.06]"
            >
              Anterior
            </button>
            {getPageRange(currentPage, totalPages).map((p, i) =>
              p === "..." ? (
                <span key={`e${i}`} className="h-8 w-8 flex items-center justify-center text-white/30 text-xs select-none">
                  ...
                </span>
              ) : (
                <button
                  key={p}
                  onClick={() => setPage(p)}
                  className={`h-8 w-8 rounded-lg text-sm font-medium transition-all ${
                    p === currentPage
                      ? "bg-white/[0.1] text-white border border-white/[0.1]"
                      : "text-white/40 hover:text-white/70 hover:bg-white/[0.04] border border-transparent"
                  }`}
                >
                  {p}
                </button>
              )
            )}
            <button
              onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
              disabled={currentPage >= totalPages}
              className="px-3 py-1.5 rounded-lg text-sm font-medium bg-white/[0.04] text-white/60 hover:bg-white/[0.08] disabled:opacity-30 disabled:cursor-not-allowed transition-all border border-white/[0.06]"
            >
              Siguiente
            </button>
          </div>
        </div>
      )}

      {showModal && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4" onClick={closeModal}>
          <div className="bg-[#1e293b] rounded-2xl p-6 sm:p-8 w-full max-w-md shadow-2xl border border-white/[0.08]" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-['Work_Sans'] font-bold text-xl text-white mb-6">
              {editingUser ? "Editar usuario" : "Nuevo usuario"}
            </h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-white/80 mb-1">Nombre completo <span className="text-red-400">*</span></label>
                <input
                  type="text"
                  value={form.fullName}
                  onChange={(e) => { setForm({ ...form, fullName: e.target.value }); clearError("fullName") }}
                  className={`w-full px-3 py-2.5 rounded-lg border text-sm focus:outline-none focus:ring-2 focus:ring-primary bg-white/[0.05] text-white placeholder:text-white/30 ${errors.fullName ? "border-red-400" : "border-white/[0.1]"}`}
                />
                {errors.fullName && <p className="text-red-400 text-xs mt-1">{errors.fullName}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-white/80 mb-1">Email <span className="text-red-400">*</span></label>
                <input
                  type="email"
                  value={form.email}
                  onChange={(e) => { setForm({ ...form, email: e.target.value }); clearError("email") }}
                  disabled={!!editingUser}
                  className={`w-full px-3 py-2.5 rounded-lg border text-sm focus:outline-none focus:ring-2 focus:ring-primary bg-white/[0.05] text-white placeholder:text-white/30 opacity-50 ${errors.email ? "border-red-400" : "border-white/[0.1]"}`}
                />
                {errors.email && <p className="text-red-400 text-xs mt-1">{errors.email}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-white/80 mb-1">{editingUser ? "Nueva contraseña (dejar vacío para mantener)" : "Contraseña"} <span className="text-red-400">*</span></label>
                <input
                  type="password"
                  value={form.password}
                  onChange={(e) => { setForm({ ...form, password: e.target.value }); clearError("password") }}
                  className={`w-full px-3 py-2.5 rounded-lg border text-sm focus:outline-none focus:ring-2 focus:ring-primary bg-white/[0.05] text-white placeholder:text-white/30 ${errors.password ? "border-red-400" : "border-white/[0.1]"}`}
                />
                {errors.password && <p className="text-red-400 text-xs mt-1">{errors.password}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-white/80 mb-1">Rol</label>
                <select
                  value={form.role}
                  onChange={(e) => setForm({ ...form, role: e.target.value as UserRole })}
                  className="w-full px-3 py-2.5 rounded-lg border border-white/[0.1] text-sm focus:outline-none focus:ring-2 focus:ring-primary bg-white/[0.05] text-white"
                >
                  <option value="STUDENT">Estudiante</option>
                  <option value="PROFESSIONAL">Profesional</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </div>
              <div className="flex gap-3 pt-2">
                <button
                  onClick={closeModal}
                  className="flex-1 px-4 py-2.5 border border-white/[0.1] rounded-lg text-sm font-medium text-white/70 hover:bg-white/[0.08] transition-all"
                >
                  Cancelar
                </button>
                <button
                  onClick={handleSave}
                  disabled={createUser.isPending || updateUser.isPending}
                  className="flex-1 px-4 py-2.5 bg-primary text-primary-foreground rounded-lg text-sm font-semibold hover:bg-primary/90 transition-all disabled:opacity-50"
                >
                  {editingUser ? "Guardar" : "Crear"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {confirmDelete && (
        <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50 p-4" onClick={() => setConfirmDelete(null)}>
          <div className="bg-[#1e293b] rounded-2xl p-6 sm:p-8 w-full max-w-sm shadow-2xl border border-white/[0.08]" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-['Work_Sans'] font-bold text-xl text-white mb-2">¿Eliminar usuario?</h2>
            <p className="text-sm text-white/50 mb-6">Esta acción desactivará al usuario. No se eliminarán sus datos permanentemente.</p>
            <div className="flex gap-3">
              <button
                onClick={() => setConfirmDelete(null)}
                className="flex-1 px-4 py-2.5 border border-white/[0.1] rounded-lg text-sm font-medium text-white/70 hover:bg-white/[0.08] transition-all"
              >
                Cancelar
              </button>
              <button
                onClick={() => handleDelete(confirmDelete)}
                disabled={deleteUser.isPending}
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
