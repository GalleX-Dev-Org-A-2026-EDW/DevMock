import type { AnswerOptionDto } from "@/api/questions"

type Props = {
  type: "SINGLE_CHOICE" | "MULTIPLE_CHOICE"
  options: AnswerOptionDto[]
  selectedId: string | string[]
  onChange: (id: string | string[]) => void
}

export default function ChoiceQuestion({ type, options, selectedId, onChange }: Props) {
  const sorted = (options ?? []).sort((a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0))

  if (type === "SINGLE_CHOICE") {
    const selected = selectedId as string
    return (
      <div className="space-y-2">
        {sorted.map((opt) => (
          <button
            key={opt.id}
            type="button"
            onClick={() => onChange(opt.id!)}
            className={`flex w-full cursor-pointer items-center gap-3 rounded-lg border px-4 py-3 text-left transition-all ${
              selected === opt.id
                ? "border-emerald-500/50 bg-emerald-500/10 ring-1 ring-emerald-500/30"
                : "border-white/10 bg-white/5 hover:border-white/20"
            }`}
          >
            <div
              className={`flex h-5 w-5 shrink-0 items-center justify-center rounded-full border-2 transition-colors ${
                selected === opt.id ? "border-emerald-500 bg-emerald-500" : "border-white/30"
              }`}
            >
              {selected === opt.id && <div className="h-2 w-2 rounded-full bg-white" />}
            </div>
            <span className="text-sm text-white">{opt.optionText}</span>
          </button>
        ))}
      </div>
    )
  }

  const selectedArr = (selectedId as string[]) ?? []
  return (
    <div className="space-y-2">
      {sorted.map((opt) => {
        const isChecked = selectedArr.includes(opt.id!)
        return (
          <button
            key={opt.id}
            type="button"
            onClick={() => {
              if (isChecked) {
                onChange(selectedArr.filter((id) => id !== opt.id))
              } else {
                onChange([...selectedArr, opt.id!])
              }
            }}
            className={`flex w-full cursor-pointer items-center gap-3 rounded-lg border px-4 py-3 text-left transition-all ${
              isChecked
                ? "border-emerald-500/50 bg-emerald-500/10 ring-1 ring-emerald-500/30"
                : "border-white/10 bg-white/5 hover:border-white/20"
            }`}
          >
            <div
              className={`flex h-5 w-5 shrink-0 items-center justify-center rounded border-2 transition-colors ${
                isChecked ? "border-emerald-500 bg-emerald-500" : "border-white/30"
              }`}
            >
              {isChecked && (
                <svg className="h-3 w-3 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={3}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                </svg>
              )}
            </div>
            <span className="text-sm text-white">{opt.optionText}</span>
          </button>
        )
      })}
    </div>
  )
}
