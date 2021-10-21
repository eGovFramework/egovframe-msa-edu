import Loader from '@components/Loader'
import React, { useEffect, useRef, useState } from 'react'

export interface IEditor {
  contents: string
  setContents: (data: string) => void
  readonly?: boolean
}

const Editor = (props: IEditor) => {
  const { contents, setContents, readonly = false } = props
  const editorRef = useRef<any>()
  const [editorLoaded, setEditorLoaded] = useState<boolean>(false)
  const { CKEditor, ClassicEditor } = editorRef.current || {}

  useEffect(() => {
    editorRef.current = {
      CKEditor: require('@ckeditor/ckeditor5-react').CKEditor,
      ClassicEditor: require('@ckeditor/ckeditor5-build-classic'),
    }

    setEditorLoaded(true)
  }, [])

  return (
    <>
      {editorLoaded ? (
        <div>
          <div id="editor" className={readonly ? 'editor-readonly' : ''}>
            <CKEditor
              editor={ClassicEditor}
              data={contents}
              disabled={readonly}
              config={{
                ckfinder: {
                  uploadUrl: `/api/editor`,
                },
                isReadOnly: readonly,
              }}
              onReady={(editor: any) => {
                console.info('editor is ready to use', editor)
              }}
              onChange={(event: any, editor: any) => {
                let chanagedata = editor.getData()
                setContents(chanagedata)
              }}
              onBlur={(event: any, editor: any) => {
                console.info('Blur.', editor)
              }}
              onFocus={(event: any, editor: any) => {
                console.info('Focus.', editor)
              }}
            />
          </div>
        </div>
      ) : (
        <Loader />
      )}
    </>
  )
}

export default Editor
