import { useEffect, useRef } from "react"
import { EditorView, basicSetup } from "codemirror"
import { EditorState } from "@codemirror/state"
import { python } from "@codemirror/lang-python"
import { java } from "@codemirror/lang-java"
import { sql } from "@codemirror/lang-sql"
import { oneDark } from "@codemirror/theme-one-dark"

type Language = "python" | "java" | "sql"

type Props = {
  value: string
  onChange: (value: string) => void
  language?: Language
}

const langExtensions: Record<string, ReturnType<typeof python>> = {
  python: python(),
  java: java(),
  sql: sql(),
}

export default function CodeEditor({ value, onChange, language = "python" }: Props) {
  const containerRef = useRef<HTMLDivElement>(null)
  const viewRef = useRef<EditorView | null>(null)
  const onChangeRef = useRef(onChange)
  const valueRef = useRef(value)

  useEffect(() => {
    onChangeRef.current = onChange
  })
  useEffect(() => {
    valueRef.current = value
  })

  useEffect(() => {
    if (!containerRef.current) return

    const view = new EditorView({
      state: EditorState.create({
        doc: valueRef.current,
        extensions: [
          basicSetup,
          oneDark,
          langExtensions[language] ?? python(),
          EditorView.updateListener.of((update) => {
            if (update.docChanged) {
              onChangeRef.current(update.state.doc.toString())
            }
          }),
        ],
      }),
      parent: containerRef.current,
    })

    viewRef.current = view

    return () => view.destroy()
  }, [language])

  useEffect(() => {
    const view = viewRef.current
    if (!view) return
    const current = view.state.doc.toString()
    if (value !== current) {
      view.dispatch({
        changes: { from: 0, to: current.length, insert: value },
      })
    }
  }, [value])

  return (
    <div
      ref={containerRef}
      className="overflow-hidden rounded-lg border border-white/20 [&_.cm-editor]:outline-none [&_.cm-content]:font-mono [&_.cm-content]:text-sm [&_.cm-gutters]:border-r-0 [&_.cm-activeLineGutter]:bg-transparent"
    />
  )
}
