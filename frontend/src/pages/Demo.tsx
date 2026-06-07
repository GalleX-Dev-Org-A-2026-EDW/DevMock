import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { SearchDemo } from "@/components/demo/SearchDemo"
import { HeavyListDemo } from "@/components/demo/HeavyListDemo"

export default function Demo() {
  return (
    <div className="mx-auto grid max-w-5xl grid-cols-1 gap-6 px-4 py-12 lg:grid-cols-2">
      <Card className="lg:col-span-2">
        <CardHeader>
          <CardTitle>useDebouncedValue</CardTitle>
          <CardDescription>
            Reduce las llamadas a funciones esperando una pausa antes de
            confirmar el valor. El valor "debounced" se mantiene estable
            mientras el usuario escribe — la búsqueda solo se ejecuta
            tras 400ms de pausa.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <SearchDemo />
        </CardContent>
      </Card>

      <Card className="lg:col-span-2">
        <CardHeader>
          <CardTitle>useDeferredValue</CardTitle>
          <CardDescription>
            Mantiene el input responsive durante renderizados costosos.
            El valor del input se actualiza al instante con cada tecla,
            pero la tabla filtrada solo se renderiza cuando el navegador
            tiene ciclos disponibles. Observa el indicador "renderizando…"
            al escribir rápido.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <HeavyListDemo />
        </CardContent>
      </Card>
    </div>
  )
}
