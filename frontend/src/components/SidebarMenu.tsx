type Props = {
  current: string;
  onChange: (page: string) => void;
};

export default function SidebarMenu({ current, onChange }: Props) {
  return (
    <div className="space-y-4">
      <h2 className="text-xl font-bold">DevMock</h2>
      <nav className="flex flex-col gap-1">
        <button
          className={`text-left rounded-md px-3 py-2 text-sm font-medium transition-colors ${
            current === "default"
              ? "bg-neutral-900 text-white"
              : "text-neutral-700 hover:bg-neutral-100"
          }`}
          onClick={() => onChange("default")}
        >
          Inicio
        </button>
      </nav>
    </div>
  )
}
