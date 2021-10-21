import Loader from '@components/Loader'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import React, { useEffect, useRef, useState } from 'react'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      paddingTop: theme.spacing(1),
      '& .ck-editor__editable_inline': {
        minHeight: '200px',
      },
    },
  }),
)

export interface IEditor {
  contents: string
  setContents: (data: string) => void
}

const Editor = (props: IEditor) => {
  const { contents, setContents } = props
  const classes = useStyles()
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
        <div className={classes.root}>
          <CKEditor
            editor={ClassicEditor}
            data={contents}
            config={{
              ckfinder: {
                uploadUrl: `/api/editor`,
              },
            }}
            onReady={(editor: any) => {
              console.info('editor is ready to use', editor)
            }}
            onChange={(event: any, editor: any) => {
              const chanagedata = editor.getData()
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
      ) : (
        <Loader />
      )}
    </>
  )
}

export default Editor
