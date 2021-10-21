import IconButton from '@material-ui/core/IconButton'
import { createStyles, makeStyles, Theme } from '@material-ui/core/styles'
import ClearIcon from '@material-ui/icons/Clear'
import { fileService, IAttachmentResponse } from '@service'
import { formatBytes } from '@utils'
import produce from 'immer'
import React from 'react'
import { useTranslation } from 'react-i18next'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    icon: {
      padding: '0px 12px',
    },
  }),
)

interface AttachListProps {
  data: IAttachmentResponse[]
  setData: React.Dispatch<React.SetStateAction<IAttachmentResponse[]>>
  readonly?: true
}

const AttachList = (props: AttachListProps) => {
  const { data, setData, readonly } = props
  const classes = useStyles()
  const { t } = useTranslation()

  const handleDelete = (item: IAttachmentResponse) => {
    setData(
      produce(data, draft => {
        const idx = draft.findIndex(attachment => attachment.id === item.id)
        draft[idx].isDelete = true
      }),
    )
  }
  return (
    <>
      {data &&
        data.map(file => {
          return file.isDelete ? null : (
            <div key={`file-div-${file.id}`} id="attach-div">
              <a
                id="attach-list"
                key={`file-${file.id}`}
                href={`${fileService.downloadUrl}/${file.id}`}
                download={file.originalFileName}
              >
                {`${file.originalFileName} (${formatBytes(file.size)})`}
              </a>
              {!readonly && (
                <IconButton
                  className={classes.icon}
                  key={`file-clear-${file.id}`}
                  onClick={(e: React.MouseEvent) => {
                    handleDelete(file)
                  }}
                >
                  <ClearIcon fontSize="inherit" />
                </IconButton>
              )}
            </div>
          )
        })}
    </>
  )
}

export default AttachList
