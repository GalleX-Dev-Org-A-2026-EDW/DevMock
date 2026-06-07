import { useRef } from "react"
import { ArrowLeft, Loader2, Save, User } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useMe, useUpdateMe } from "@/api/users.queries"
import { useDifficultyLevels } from "@/api/difficulty-levels.queries"

type Props = {
  onBack: () => void
}

export default function ProfileEditView({ onBack }: Props) {
  const { data: user, isLoading: userLoading } = useMe()
  const { data: difficulties } = useDifficultyLevels()
  const updateMe = useUpdateMe()

  const fullNameRef = useRef<HTMLInputElement>(null)
  const avatarUrlRef = useRef<HTMLInputElement>(null)
  const professionalYearsRef = useRef<HTMLInputElement>(null)
  const currentLevelRef = useRef<HTMLSelectElement>(null)

  const handleSave = async () => {
    await updateMe.mutateAsync({
      fullName: fullNameRef.current?.value || undefined,
      avatarUrl: avatarUrlRef.current?.value || undefined,
      professionalExperienceYears: professionalYearsRef.current?.value
        ? Number(professionalYearsRef.current.value)
        : undefined,
      currentLevelId: currentLevelRef.current?.value || undefined,
    })
  }

  if (userLoading) {
    return (
      <div className="flex items-center justify-center py-32">
        <div className="text-center">
          <div className="mx-auto mb-3 h-8 w-8 animate-spin rounded-full border-2 border-emerald-500 border-t-transparent" />
          <p className="text-sm text-white/60">Cargando perfil...</p>
        </div>
      </div>
    )
  }

  const inputClass =
    "flex h-10 w-full rounded-md border border-white/20 bg-white/10 px-3 py-2 text-sm text-white placeholder:text-white/40 focus:outline-none focus:ring-2 focus:ring-white/30 transition-all"

  const disabledClass =
    "flex h-10 w-full rounded-md border border-white/10 bg-white/5 px-3 py-2 text-sm text-white/40 cursor-not-allowed"

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button
            onClick={onBack}
            className="rounded-lg p-2 text-white/60 transition-colors hover:bg-white/10 hover:text-white"
            aria-label="Volver al inicio"
          >
            <ArrowLeft className="h-5 w-5" />
          </button>
          <div>
            <h1 className="font-['Work_Sans'] text-2xl font-bold text-white">Mi perfil</h1>
            <p className="mt-1 text-sm text-white/60">Información personal y preferencias.</p>
          </div>
        </div>
      </div>

      <div className="grid gap-6 xl:grid-cols-2">
        <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
          <h2 className="font-['Work_Sans'] mb-5 text-lg font-semibold text-white">Información personal</h2>
          <div className="space-y-4">
            <div>
              <p className="mb-1.5 text-sm text-white/50">Nombre completo</p>
              <input
                ref={fullNameRef}
                defaultValue={user?.fullName ?? ""}
                placeholder="Tu nombre"
                className={inputClass}
              />
            </div>
            <div>
              <p className="mb-1.5 text-sm text-white/50">Email</p>
              <input value={user?.email ?? ""} disabled className={disabledClass} />
            </div>
            <div>
              <p className="mb-1.5 text-sm text-white/50">Rol</p>
              <input
                value={
                  user?.role === "STUDENT"
                    ? "Estudiante"
                    : user?.role === "PROFESSIONAL"
                      ? "Profesional"
                      : user?.role ?? ""
                }
                disabled
                className={disabledClass}
              />
            </div>
            <div>
              <p className="mb-1.5 text-sm text-white/50">URL de avatar</p>
              <input
                ref={avatarUrlRef}
                defaultValue={user?.avatarUrl ?? ""}
                placeholder="https://ejemplo.com/avatar.png"
                className={inputClass}
              />
            </div>
          </div>
        </div>

        <div className="rounded-lg border border-white/10 bg-white/10 p-5 backdrop-blur-sm">
          <h2 className="font-['Work_Sans'] mb-5 text-lg font-semibold text-white">Preferencias</h2>
          <div className="space-y-4">
            <div>
              <p className="mb-1.5 text-sm text-white/50">Nivel de dificultad actual</p>
              <select
                ref={currentLevelRef}
                defaultValue={user?.currentLevelId ?? ""}
                className="flex h-10 w-full rounded-md border border-white/20 bg-white/10 px-3 py-2 text-sm text-white focus:outline-none focus:ring-2 focus:ring-white/30 transition-all"
              >
                <option value="" className="bg-[#1a1a2e]">Sin definir</option>
                {difficulties?.map((d) => (
                  <option key={d.id} value={d.id} className="bg-[#1a1a2e]">{d.name}</option>
                ))}
              </select>
            </div>
            <div>
              <p className="mb-1.5 text-sm text-white/50">Años de experiencia profesional</p>
              <input
                ref={professionalYearsRef}
                type="number"
                min={0}
                defaultValue={user?.professionalExperienceYears?.toString() ?? ""}
                placeholder="0"
                className={inputClass}
              />
            </div>
            <div className="rounded-lg border border-white/10 bg-white/10 p-3">
              <p className="text-xs text-white/60">
                Los campos deshabilitados no pueden modificarse desde aquí.
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          {user?.avatarUrl && (
            <div className="flex items-center gap-2 rounded-lg border border-white/10 bg-white/10 px-3 py-2">
              <User className="h-4 w-4 text-white/50" />
              <span className="text-xs text-white/50">Avatar configurado</span>
            </div>
          )}
        </div>
        <div className="flex gap-3">
          <Button variant="outline" onClick={onBack} className="bg-transparent">
            Cancelar
          </Button>
          <Button onClick={handleSave} disabled={updateMe.isPending} className="gap-2">
            {updateMe.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <Save className="h-4 w-4" />}
            Guardar cambios
          </Button>
        </div>
      </div>

      {updateMe.isSuccess && (
        <div className="rounded-lg bg-emerald-500/20 p-3 text-sm text-emerald-300">
          Perfil actualizado correctamente.
        </div>
      )}

      {updateMe.isError && (
        <div className="rounded-lg bg-red-500/20 p-3 text-sm text-red-300">
          {updateMe.error instanceof Error ? updateMe.error.message : "Error al guardar los cambios."}
        </div>
      )}
    </div>
  )
}
