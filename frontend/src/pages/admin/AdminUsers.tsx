import { useState } from "react"
import { useUsers, useCreateUser, useUpdateUser, useDeleteUser } from "@/api/users.queries"
import type { User, CreateUserDto, UpdateUserDto } from "@/api/users"
import type { UserRole } from "@/api/enums"
import { Plus, Edit2, Trash2, UserCheck, UserX, Shield } from "lucide-react"

export default function AdminUsers() {
  const { data: users, isLoading, isError } = useUsers()
  const createUser = useCreateUser()
  const updateUser = useUpdateUser()
  const deleteUser = useDeleteUser()

  const [showModal, setShowModal] = useState(false)
  const [editingUser, setEditingUser] = useState<User | null>(null)
  const [confirmDelete, setConfirmDelete] = useState<string | null>(null)

  const [form, setForm] = useState<CreateUserDto>({
    email: "",
    password: "",
    fullName: "",
    role: "STUDENT" as UserRole,
  })

  const openCreate = () => {
    setEditingUser(null)
    setForm({ email: "", password: "", fullName: "", role: "STUDENT" })
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
    setShowModal(true)
  }

  const handleSave = async () => {
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
        <div className="h-8 w-8 border-2 border-emerald-500 border-t-transparent rounded-full animate-spin" />
      </div>
    )
  }

  if (isError) {
    return <p className="text-red-400">Error al cargar usuarios</p>
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="font-['Work_Sans'] font-bold text-2xl text-white">Usuarios</h1>
        <button
          onClick={openCreate}
          className="px-4 py-2 bg-emerald-500 text-white rounded-lg text-sm font-semibold hover:bg-emerald-600 transition-all flex items-center gap-2"
        >
          <Plus className="h-4 w-4" /> Nuevo usuario
        </button>
      </div>

      <div className="bg-white rounded-xl border border-gray-100 shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-gray-100 bg-gray-50/50">
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Nombre</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Email</th>
                <th className="text-left px-4 py-3 font-semibold text-gray-600">Rol</th>
                <th className="text-center px-4 py-3 font-semibold text-gray-600">Activo</th>
                <th className="text-center px-4 py-3 font-semibold text-gray-600">Verificado</th>
                <th className="text-right px-4 py-3 font-semibold text-gray-600">Acciones</th>
              </tr>
            </thead>
            <tbody>
              {users?.map((user) => (
                <tr key={user.id} className="border-b border-gray-50 hover:bg-gray-50/50 transition-colors">
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-3">
                      <div className="h-8 w-8 rounded-full bg-gradient-to-br from-emerald-400 to-blue-500 flex items-center justify-center text-white text-xs font-bold">
                        {user.fullName.charAt(0).toUpperCase()}
                      </div>
                      <span className="font-medium text-gray-900">{user.fullName}</span>
                    </div>
                  </td>
                  <td className="px-4 py-3 text-gray-500">{user.email}</td>
                  <td className="px-4 py-3">
                    <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                      user.role === "ADMIN" ? "bg-rose-50 text-rose-700" :
                      user.role === "PROFESSIONAL" ? "bg-violet-50 text-violet-700" :
                      "bg-emerald-50 text-emerald-700"
                    }`}>
                      {user.role === "STUDENT" ? "Estudiante" : user.role === "PROFESSIONAL" ? "Profesional" : "Admin"}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-center">
                    <button onClick={() => toggleActive(user)} className="transition-colors">
                      {user.isActive ? <UserCheck className="h-5 w-5 text-emerald-500" /> : <UserX className="h-5 w-5 text-red-400" />}
                    </button>
                  </td>
                  <td className="px-4 py-3 text-center">
                    {user.isVerified ? <Shield className="h-5 w-5 text-emerald-500 mx-auto" /> : <Shield className="h-5 w-5 text-gray-300 mx-auto" />}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button onClick={() => openEdit(user)} className="p-1.5 rounded-lg hover:bg-gray-100 transition-colors">
                        <Edit2 className="h-4 w-4 text-gray-400" />
                      </button>
                      <button onClick={() => setConfirmDelete(user.id)} className="p-1.5 rounded-lg hover:bg-red-50 transition-colors">
                        <Trash2 className="h-4 w-4 text-red-400" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {(!users || users.length === 0) && (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-gray-400">No hay usuarios registrados</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-6" onClick={() => setShowModal(false)}>
          <div className="bg-white rounded-2xl p-8 w-full max-w-md shadow-2xl" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-['Work_Sans'] font-bold text-xl text-gray-900 mb-6">
              {editingUser ? "Editar usuario" : "Nuevo usuario"}
            </h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Nombre completo</label>
                <input
                  type="text"
                  value={form.fullName}
                  onChange={(e) => setForm({ ...form, fullName: e.target.value })}
                  className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                <input
                  type="email"
                  value={form.email}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                  disabled={!!editingUser}
                  className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary disabled:bg-gray-50"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">{editingUser ? "Nueva contraseña (dejar vacío para mantener)" : "Contraseña"}</label>
                <input
                  type="password"
                  value={form.password}
                  onChange={(e) => setForm({ ...form, password: e.target.value })}
                  className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Rol</label>
                <select
                  value={form.role}
                  onChange={(e) => setForm({ ...form, role: e.target.value as UserRole })}
                  className="w-full px-3 py-2.5 rounded-lg border border-gray-200 text-sm focus:outline-none focus:ring-2 focus:ring-primary"
                >
                  <option value="STUDENT">Estudiante</option>
                  <option value="PROFESSIONAL">Profesional</option>
                  <option value="ADMIN">Admin</option>
                </select>
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
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-6" onClick={() => setConfirmDelete(null)}>
          <div className="bg-white rounded-2xl p-8 w-full max-w-sm shadow-2xl" onClick={(e) => e.stopPropagation()}>
            <h2 className="font-['Work_Sans'] font-bold text-xl text-gray-900 mb-2">¿Eliminar usuario?</h2>
            <p className="text-sm text-gray-500 mb-6">Esta acción desactivará al usuario. No se eliminarán sus datos permanentemente.</p>
            <div className="flex gap-3">
              <button
                onClick={() => setConfirmDelete(null)}
                className="flex-1 px-4 py-2.5 border border-gray-200 rounded-lg text-sm font-medium text-gray-700 hover:bg-gray-50 transition-all"
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
